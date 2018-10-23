
package controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.RendezvousService;
import services.UserService;
import controllers.AbstractController;
import domain.Actor;
import domain.Rendezvous;
import domain.User;

@Controller
@RequestMapping("/actor")
public class ActorUserController extends AbstractController {

	@Autowired
	ActorService		actorService;

	@Autowired
	UserService			userService;

	@Autowired
	RendezvousService	rendezvousService;


	//Edit User
	@RequestMapping(value = "/user/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(User user, final BindingResult binding) {
		ModelAndView result;

		Assert.isTrue(user.equals(this.userService.findByPrincipal()));

		user = this.userService.reconstruct(user, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(user);
		else
			try {
				this.userService.save(user);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(user, "actor.commit.error");
			}
		return result;
	}

	//Delete an user
	@RequestMapping(value = "/user/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final int actorId) {
		ModelAndView result;
		final User user = this.userService.findOne(actorId);
		Assert.isTrue(user.equals(this.userService.findByPrincipal()));
		try {
			for (final Rendezvous r : user.getRendezvouses())
				this.rendezvousService.delete(r);
			this.userService.delete(user);
			result = new ModelAndView("redirect:../../j_spring_security_logout");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(user, "actor.commit.error");
		}
		return result;
	}
	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor, final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		result.addObject("actor", actor);
		return result;
	}
}
