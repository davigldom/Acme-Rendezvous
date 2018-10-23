
package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AnswerService;
import services.CategoryService;
import services.QuestionService;
import services.RendezvousService;
import services.UserService;
import domain.Actor;
import domain.Answer;
import domain.Category;
import domain.Question;
import domain.Rendezvous;
import domain.Request;
import domain.User;

@Controller
@RequestMapping("/rendezvous")
public class RendezvousController extends AbstractController {

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private UserService			userService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private QuestionService		questionService;

	@Autowired
	private AnswerService		answerService;

	@Autowired
	private CategoryService		categoryService;


	public RendezvousController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int rendezvousId) {
		ModelAndView result;
		Rendezvous rendezvous;
		Collection<Rendezvous> linkedRendezvouses;
		Collection<Request> requests;
		boolean isRSVPing = false;

		rendezvous = this.rendezvousService.findOne(rendezvousId);

		linkedRendezvouses = this.rendezvousService.findLinked(rendezvousId);

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.getAuthorities().toArray()[0].toString().equals("ADMIN") && (rendezvous.isAdultOnly() && (!this.userService.isAuthenticated() || !this.actorService.isAdult())))
			result = new ModelAndView("rendezvous/list");
		else {

			Assert.notNull(rendezvous);
			result = new ModelAndView("rendezvous/display");

			final boolean loggedIn = this.userService.isAuthenticated();
			if (loggedIn) {
				final Actor principal = this.actorService.findByPrincipal();
				if (!rendezvous.isDeleted()) {
					final Collection<Rendezvous> notLinked = this.rendezvousService.findAllAvailableToLinkByCreator(principal.getId(), rendezvous);
					result.addObject("notLinked", notLinked);
				}
				final Collection<User> attendants = rendezvous.getAttendants();
				if (attendants.contains(principal))
					isRSVPing = true;
			}

			result.addObject("isRSVPing", isRSVPing);

			boolean isAdult = true;
			if (!authentication.getAuthorities().toArray()[0].toString().equals("ADMIN") && (!this.userService.isAuthenticated() || !this.actorService.isAdult()))
				isAdult = false;

			final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			final String momentFormated = formatter.format(rendezvous.getMoment());

			result.addObject("momentFormated", momentFormated);
			result.addObject("rendezvous", rendezvous);
			result.addObject("isAdult", isAdult);

			final Question question = this.questionService.create(rendezvousId);
			Collection<Question> questions;

			questions = this.questionService.findAllByRendezvous(rendezvousId);
			result.addObject("question", question);
			result.addObject("questions", questions);
			result.addObject("rendezvousId", rendezvousId);

			final Answer answer = this.answerService.create();
			result.addObject("answer", answer);

			Collection<Answer> answers = new ArrayList<Answer>();
			if (loggedIn && !authentication.getAuthorities().toArray()[0].toString().equals("ADMIN") && !authentication.getAuthorities().toArray()[0].toString().equals("MANAGER"))
				answers = this.userService.findByPrincipal().getAnswers();
			result.addObject("answers", answers);

		}
		result.addObject("linkedRendezvouses", linkedRendezvouses);

		requests = rendezvous.getRequests();
		result.addObject("requests", requests);

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView listAll() {
		ModelAndView result;
		Collection<Rendezvous> rendezvouses;
		final Date now = new Date();

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = null;

		if (!authentication.getAuthorities().toArray()[0].toString().equals("USER") && !authentication.getAuthorities().toArray()[0].toString().equals("ADMIN"))
			rendezvouses = this.rendezvousService.findAllNoAdult();
		else if (authentication.getAuthorities().toArray()[0].toString().equals("ADMIN"))
			rendezvouses = this.rendezvousService.findAll();
		else {
			final boolean isAdult = this.actorService.isAdult();

			if (isAdult == false)
				rendezvouses = this.rendezvousService.findAllNoAdult();
			else
				rendezvouses = this.rendezvousService.findAll();
		}

		if (authentication.getAuthorities().toArray()[0].toString().equals("USER"))
			principal = this.userService.findByPrincipal();

		result = new ModelAndView("rendezvous/list");
		result.addObject("rendezvouses", rendezvouses);
		result.addObject("principal", principal);
		result.addObject("requestURI", "rendezvous/list.do");
		result.addObject("isListingRSVPd", false);
		result.addObject("isListingCreated", false);
		result.addObject("now", now);

		return result;
	}

	@RequestMapping(value = "/list-by-category", method = RequestMethod.GET)
	public ModelAndView listByCategory(@RequestParam final int categoryId) {
		ModelAndView result;
		Collection<Rendezvous> rendezvouses;
		final Date now = new Date();

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = null;

		if (!authentication.getAuthorities().toArray()[0].toString().equals("USER") && !authentication.getAuthorities().toArray()[0].toString().equals("ADMIN"))
			rendezvouses = this.rendezvousService.findNoAdultByCategory(categoryId);
		else {
			final boolean isAdult = this.actorService.isAdult();

			if (isAdult == false)
				rendezvouses = this.rendezvousService.findNoAdultByCategory(categoryId);
			else
				rendezvouses = this.rendezvousService.findByCategory(categoryId);
		}

		if (authentication.getAuthorities().toArray()[0].toString().equals("USER"))
			principal = this.userService.findByPrincipal();

		final Category category = this.categoryService.findOne(categoryId);

		result = new ModelAndView("rendezvous/list-by-category");
		result.addObject("rendezvouses", rendezvouses);
		result.addObject("category", category);
		result.addObject("principal", principal);
		result.addObject("requestURI", "rendezvous/list.do");
		result.addObject("isListingRSVPd", false);
		result.addObject("isListingCreated", false);
		result.addObject("now", now);

		return result;
	}

}
