
package controllers;

import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ManagerService;
import services.RendezvousService;
import services.UserService;
import domain.Actor;
import domain.Manager;
import domain.Rendezvous;
import domain.User;
import forms.RegisterManager;
import forms.RegisterUser;

@Controller
@RequestMapping("/actor")
public class ActorController extends AbstractController {

	@Autowired
	ActorService		actorService;

	@Autowired
	UserService			userService;

	@Autowired
	ManagerService		managerService;

	@Autowired
	RendezvousService	rendezvousService;


	//Create User
	@RequestMapping(value = "/create-user", method = RequestMethod.GET)
	public ModelAndView createUser() {
		ModelAndView result;
		RegisterUser registerUser;

		registerUser = new RegisterUser();
		result = this.createEditModelAndViewRegisterUser(registerUser, null);
		return result;
	}

	//Create Manager
	@RequestMapping(value = "/create-manager", method = RequestMethod.GET)
	public ModelAndView createManager() {
		ModelAndView result;
		RegisterManager registerManager;

		registerManager = new RegisterManager();
		result = this.createEditModelAndViewRegisterManager(registerManager, null);
		return result;
	}

	@RequestMapping(value = "/create-user-ok", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final RegisterUser registerUser, final BindingResult binding) {
		ModelAndView result;
		final User user;

		if (binding.hasErrors()) {
			registerUser.setAcceptedTerms(false);
			result = this.createEditModelAndViewRegisterUser(registerUser, null);
		} else
			try {
				user = this.userService.reconstructRegister(registerUser, binding);
				if (binding.hasErrors())
					result = this.createEditModelAndViewRegisterUser(registerUser, null);
				else {
					this.userService.save(user);
					result = new ModelAndView("redirect:/welcome/index.do");
				}
			} catch (final Throwable oops) {
				registerUser.setAcceptedTerms(false);
				result = this.createEditModelAndViewRegisterUser(registerUser, "actor.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/create-manager-ok", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final RegisterManager registerManager, final BindingResult binding) {
		ModelAndView result;
		final Manager manager;

		if (binding.hasErrors()) {
			registerManager.setAcceptedTerms(false);
			result = this.createEditModelAndViewRegisterManager(registerManager, null);
		} else
			try {
				manager = this.managerService.reconstructRegister(registerManager, binding);
				if (binding.hasErrors())
					result = this.createEditModelAndViewRegisterManager(registerManager, null);
				else {
					this.managerService.save(manager);
					result = new ModelAndView("redirect:/welcome/index.do");
				}
			} catch (final Throwable oops) {
				registerManager.setAcceptedTerms(false);
				result = this.createEditModelAndViewRegisterManager(registerManager, "actor.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int actorId) {
		ModelAndView result;
		final Actor actor = this.actorService.findOneToEdit(actorId);

		result = this.createEditModelAndView2(actor, null);
		result.addObject("actor", actor);

		return result;
	}

	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int actorId) {
		ModelAndView result;
		Actor actor;
		actor = this.actorService.findOne(actorId);
		final String authority = actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase();
		Collection<Rendezvous> rendezvouses;

		if (this.userService.isAuthenticated() && this.actorService.isAdult())
			rendezvouses = this.rendezvousService.findRSVPdRendezvousesPerUser(actorId);
		else
			rendezvouses = this.rendezvousService.findRSVPdRendezvousesNoAdultById(actorId);

		Assert.notNull(actor);

		result = new ModelAndView("actor/display");
		result.addObject("actor", actor);
		result.addObject("rendezvouses", rendezvouses);
		result.addObject("authority", authority);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		final String dateFormated = formatter.format(actor.getBirthdate());

		result.addObject("dateFormated", dateFormated);

		return result;
	}
	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		final Collection<User> users = this.userService.findAll();
		result = new ModelAndView("actor/list");
		result.addObject("users", users);

		return result;
	}

	//Display own data
	@RequestMapping(value = "/display-principal", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Actor actor;
		actor = this.actorService.findOne(this.actorService.findByPrincipal().getId());
		final Collection<Rendezvous> rendezvouses = this.rendezvousService.findRSVPdRendezvousesPerUser(actor.getId());
		Assert.notNull(actor);
		result = new ModelAndView("actor/display");
		result.addObject("rendezvouses", rendezvouses);
		result.addObject("actor", actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());

		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		final String dateFormated = formatter.format(actor.getBirthdate());

		result.addObject("dateFormated", dateFormated);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor, final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/signup");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		return result;
	}

	protected ModelAndView createEditModelAndView2(final Actor actor, final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");

		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());

		result.addObject("message", message);
		return result;
	}

	protected ModelAndView createEditModelAndViewRegisterUser(final RegisterUser registerUser, final String message) {
		ModelAndView result;

		final String requestURI = "actor/create-user-ok.do";

		result = new ModelAndView("actor/signup");
		result.addObject("registerUser", registerUser);
		result.addObject("message", message);
		result.addObject("requestURI", requestURI);

		return result;
	}

	protected ModelAndView createEditModelAndViewRegisterManager(final RegisterManager registerManager, final String message) {
		ModelAndView result;

		final String requestURI = "actor/create-manager-ok.do";

		result = new ModelAndView("actor/signup-manager");
		result.addObject("registerManager", registerManager);
		result.addObject("message", message);
		result.addObject("requestURI", requestURI);

		return result;
	}
}
