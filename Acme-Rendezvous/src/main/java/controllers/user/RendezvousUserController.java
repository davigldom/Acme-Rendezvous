package controllers.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AnswerService;
import services.RendezvousService;
import services.UserService;
import controllers.AbstractController;
import domain.Answer;
import domain.Rendezvous;
import domain.User;

@Controller
@RequestMapping("/rendezvous/user")
public class RendezvousUserController extends AbstractController {

	@Autowired
	private RendezvousService rendezvousService;

	@Autowired
	private UserService userService;

	@Autowired
	private AnswerService answerService;

	@Autowired
	private ActorService actorService;

	public RendezvousUserController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Rendezvous> rendezvouses;
		final Date now = new Date();

		final User principal = this.userService.findByPrincipal();
		final boolean isAdult = this.actorService.isAdult();

		if (isAdult == false)
			rendezvouses = this.rendezvousService
					.findRSVPdRendezvousesNoAdult();
		else
			rendezvouses = this.rendezvousService.findRSVPdRendezvouses();

		result = new ModelAndView("rendezvous/listRSVPd");
		result.addObject("rendezvouses", rendezvouses);
		result.addObject("requestURI", "rendezvous/user/list.do");
		result.addObject("principal", principal);
		result.addObject("isListingRSVPd", true);
		result.addObject("isListingCreated", false);
		result.addObject("now", now);

		return result;
	}

	@RequestMapping(value = "/listCreated", method = RequestMethod.GET)
	public ModelAndView listCreated() {
		ModelAndView result;
		Collection<Rendezvous> rendezvouses;
		final Date now = new Date();

		final User principal = this.userService.findByPrincipal();
		final boolean isAdult = this.actorService.isAdult();

		if (isAdult == false)
			rendezvouses = this.rendezvousService
					.findCreatedRendezvousesNoAdult();
		else
			rendezvouses = this.rendezvousService.findCreatedRendezvouses();

		result = new ModelAndView("rendezvous/listCreated");
		result.addObject("rendezvouses", rendezvouses);
		result.addObject("requestURI", "rendezvous/user/listCreated.do");
		result.addObject("principal", principal);
		result.addObject("isListingRSVPd", false);
		result.addObject("isListingCreated", true);
		result.addObject("now", now);

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Rendezvous rendezvous;
		rendezvous = this.rendezvousService.create();
		result = this.createEditModelAndView(rendezvous);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int rendezvousId) {
		ModelAndView result;
		Rendezvous rendezvous;

		rendezvous = this.rendezvousService.findOneToEdit(rendezvousId);
		Assert.notNull(rendezvous);
		result = this.createEditModelAndView(rendezvous);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Rendezvous rendezvous, final BindingResult binding) {
		ModelAndView result;
		if (rendezvous.getId() != 0) {
			final Rendezvous storedRendezvous = this.rendezvousService
					.findOne(rendezvous.getId());
			Assert.isTrue(storedRendezvous.isDraft());
			Assert.isTrue(storedRendezvous.getMoment().after(new Date()));
			Assert.isTrue(!storedRendezvous.isDeleted());
		}
		rendezvous = this.rendezvousService.reconstruct(rendezvous, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(rendezvous);
		else
			try {
				final Rendezvous rSaved = this.rendezvousService
						.save(rendezvous);
				result = new ModelAndView(
						"redirect:/rendezvous/display.do?rendezvousId="
								+ rSaved.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(rendezvous,
						"rendezvous.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "link")
	public ModelAndView link(@RequestParam final int rendezvousId,
			@RequestParam("linkedId") final Set<String> linkedIds) {
		ModelAndView result;
		final Collection<Rendezvous> list = new HashSet<Rendezvous>();
		Rendezvous rd = this.rendezvousService.findOne(rendezvousId);
		try {
			rd = this.rendezvousService.findOneToLink(rendezvousId);
			for (final String id : linkedIds) {
				Assert.isTrue(Integer.valueOf(id)!=rendezvousId);
				final Rendezvous aux = this.rendezvousService
						.findOneToLink(Integer.valueOf(id));
				list.add(aux);
				aux.getLinked().add(rd);
				this.rendezvousService.link(aux);
			}
			rd.getLinked().addAll(list);
			this.rendezvousService.link(rd);
			result = new ModelAndView(
					"redirect:/rendezvous/display.do?rendezvousId="
							+ rendezvousId);
		} catch (final Throwable oops) {

			result = new ModelAndView(
					"redirect:/rendezvous/display.do?rendezvousId="
							+ rendezvousId);
			result.addObject("message", "rendezvous.link.error");
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "unlink")
	public ModelAndView unlink(@RequestParam final int rendezvousIdMain,
			@RequestParam final int rendezvousIdAux) {
		ModelAndView result;
		final Rendezvous main = this.rendezvousService
				.findOne(rendezvousIdMain);
		final Rendezvous aux = this.rendezvousService.findOne(rendezvousIdAux);
		this.rendezvousService.checkAreLinked(main, aux);

		try {
			this.rendezvousService.unLink(main, aux);
			result = new ModelAndView(
					"redirect:/rendezvous/display.do?rendezvousId="
							+ rendezvousIdMain);
		} catch (final Throwable oops) {
			result = this
					.createEditModelAndView2(main, "rendezvous.link.error");
		}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Rendezvous rendezvous,
			final BindingResult binding) {
		ModelAndView result;
		final Rendezvous storedRendezvous = this.rendezvousService
				.findOne(rendezvous.getId());
		Assert.isTrue(storedRendezvous.isDraft());
		Assert.isTrue(storedRendezvous.getMoment().after(new Date()));
		rendezvous = this.rendezvousService.reconstruct(rendezvous, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(rendezvous);
		else
			try {
				this.rendezvousService.virtualDelete(rendezvous);
				result = new ModelAndView("redirect:/rendezvous/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(rendezvous,
						"rendezvous.commit.error");
			}

		return result;
	}

	// Used when an user RSVPs a rendezvous without questions
	@RequestMapping(value = "/RSVP", method = RequestMethod.GET)
	public ModelAndView RSVP(@RequestParam final int rendezvousId) {
		ModelAndView result;

		try {
			this.rendezvousService.RSVP(rendezvousId);
			result = this.list();
			result.addObject("message", "rendezvous.commit.ok");
		} catch (final Throwable oops) {
			result = this.list();
			result.addObject("message", "rendezvous.commit.error");
		}

		return result;
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public ModelAndView cancel(@RequestParam final int rendezvousId) {
		ModelAndView result;
		final List<Answer> answers = new ArrayList<Answer>(
				this.answerService.findAllByRendezvous(rendezvousId));

		try {
			for (final Answer a : answers) {
				a.getQuestion().getAnswers().remove(a);
				this.answerService.delete(a);
			}
			this.rendezvousService.cancel(rendezvousId);
			result = this.list();
			result.addObject("message", "rendezvous.commit.ok");
		} catch (final Throwable oops) {
			result = this.list();
			result.addObject("message", "rendezvous.commit.error");
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Rendezvous r) {
		ModelAndView result;

		result = this.createEditModelAndView(r, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Rendezvous rendezvous,
			final String message) {
		ModelAndView result = null;
		boolean canSee = true;

		if (rendezvous.getId() != 0)
			result = new ModelAndView("rendezvous/edit");
		else if (rendezvous.getId() == 0)
			result = new ModelAndView("rendezvous/create");

		if (!this.actorService.isAdult())
			canSee = false;

		result.addObject("rendezvous", rendezvous);
		result.addObject("canSee", canSee);

		result.addObject("message", message);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final Rendezvous rendezvous,
			final String message) {
		ModelAndView result;

		result = new ModelAndView("rendezvous/display");

		result.addObject("rendezvous", rendezvous);

		result.addObject("message", message);

		return result;
	}

}
