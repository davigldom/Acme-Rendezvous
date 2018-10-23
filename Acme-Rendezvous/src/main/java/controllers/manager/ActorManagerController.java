
package controllers.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.AcmeServiceService;
import services.ActorService;
import services.ManagerService;
import controllers.AbstractController;
import domain.Actor;
import domain.Manager;

@Controller
@RequestMapping("/actor")
public class ActorManagerController extends AbstractController {

	@Autowired
	ActorService		actorService;

	@Autowired
	ManagerService		managerService;

	@Autowired
	AcmeServiceService	acmeServiceService;


	//Edit User
	@RequestMapping(value = "/manager/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Manager manager, final BindingResult binding) {
		ModelAndView result;

		Assert.isTrue(manager.equals(this.managerService.findByPrincipal()));

		manager = this.managerService.reconstruct(manager, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(manager);
		else
			try {
				this.managerService.save(manager);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(manager, "actor.commit.error");
			}
		return result;
	}

	//Delete an user
	@RequestMapping(value = "/manager/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final int actorId) {
		ModelAndView result;
		final Manager manager = this.managerService.findOne(actorId);
		Assert.isTrue(manager.equals(this.managerService.findByPrincipal()));
		try {

			this.managerService.delete(manager);
			result = new ModelAndView("redirect:../../j_spring_security_logout");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(manager, "actor.commit.error");
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
