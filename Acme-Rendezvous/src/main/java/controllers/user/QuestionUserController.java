
package controllers.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import services.QuestionService;
import services.RendezvousService;
import services.UserService;
import controllers.AbstractController;
import domain.Answer;
import domain.Question;
import domain.Rendezvous;
import domain.User;

@Controller
@RequestMapping("/question/user")
public class QuestionUserController extends AbstractController {

	@Autowired
	private QuestionService		questionService;

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private UserService			userService;

	@Autowired
	private AnswerService		answerService;
	
	@Autowired
	private ActorService		actorService;


	public QuestionUserController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "saveCreate")
	public ModelAndView saveCreate(@RequestParam final int rendezvousId, Question question, final BindingResult binding) {
		ModelAndView result;
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		if(rendezvous.isAdultOnly())Assert.isTrue(this.actorService.isAdult());
		question.setRendezvous(rendezvous);
		question = this.questionService.reconstruct(question, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView2(question, rendezvousId);
		else
			try {
				this.questionService.save(question);
				result = new ModelAndView("redirect:/rendezvous/display.do?rendezvousId=" + rendezvousId);
			} catch (final Throwable oops) {
				result = this.createEditModelAndView2(question, rendezvousId, "rendezvous.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "saveEdit")
	public ModelAndView saveEdit(@RequestParam final int rendezvousId, Question question, final BindingResult binding) {
		ModelAndView result;

		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		question.setRendezvous(rendezvous);
		question = this.questionService.reconstruct(question, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(question);
		else
			try {
				this.questionService.save(question);
				result = new ModelAndView("redirect:/rendezvous/display.do?rendezvousId=" + rendezvousId);
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(question, "rendezvous.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int rendezvousId) {
		ModelAndView result;
		Rendezvous rendezvous;
		Collection<Question> questions;

		rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvous.isDeleted()==false);
		Assert.isTrue(rendezvous.getMoment().after(new Date()));
		if(rendezvous.isAdultOnly())Assert.isTrue(this.actorService.isAdult());
		Assert.notNull(rendezvous);

		questions = this.questionService.findAllByRendezvous(rendezvousId);
		if (questions.isEmpty())
			result = new ModelAndView("redirect:/rendezvous/user/RSVP.do?rendezvousId=" + rendezvousId);
		else {
			result = new ModelAndView("question/display");

			final Question question = this.questionService.create(rendezvousId);

			result.addObject("question", question);
			result.addObject("questions", questions);
			result.addObject("rendezvousId", rendezvousId);

			final Answer answer = this.answerService.create();
			result.addObject("answer", answer);

			Collection<Answer> answers;
			answers = this.userService.findByPrincipal().getAnswers();
			result.addObject("answers", answers);

			result.addObject("requestURI", "question/user/display.do?rendezvousId=" + rendezvousId);
			result.addObject("yourAnswers", true);
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int questionId) {
		ModelAndView result;
		Question question;

		question = this.questionService.findOneToEdit(questionId);
		Assert.notNull(question);
		result = this.createEditModelAndView(question);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(@RequestParam int rendezvousId, Question question, final BindingResult binding) {
		ModelAndView result;

		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		question.setRendezvous(rendezvous);
		question = this.questionService.reconstruct(question, binding);

		final List<Answer> answers = new ArrayList<Answer>(this.answerService.findAllAnswersInQuestion(question.getId()));
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(question.getCreator().equals(principal));
		if (binding.hasErrors())
			result = this.createEditModelAndView(question);
		else
			try {
				for (Answer a : answers) {
					question.getAnswers().remove(a);
					this.answerService.delete(a);
				}
				final Question questionNow = this.questionService.findOne(question.getId());
				this.questionService.delete(questionNow);
				result = new ModelAndView("redirect:/rendezvous/display.do?rendezvousId=" + rendezvous.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(question, "rendezvous.commit.error");
			}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Question question) {
		ModelAndView result;

		result = this.createEditModelAndView(question, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Question question, final String message) {
		ModelAndView result = null;
		Rendezvous rendezvous;

		result = new ModelAndView("question/edit");

		result.addObject("question", question);

		rendezvous = question.getRendezvous();
		result.addObject("rendezvousId", rendezvous.getId());

		result.addObject("message", message);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final Question question, final int rendezvousId) {
		ModelAndView result;

		result = this.createEditModelAndView2(question, rendezvousId, null);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final Question question, final int rendezvousId, final String message) {
		ModelAndView result;
		Rendezvous rendezvous;
		boolean isRSVPing = false;

		rendezvous = this.rendezvousService.findOne(rendezvousId);

		Assert.notNull(rendezvous);
		result = new ModelAndView("rendezvous/display");

		final User principal = this.userService.findByPrincipal();
		final Collection<Rendezvous> notLinked = this.rendezvousService.findAllAvailableToLinkByCreator(principal.getId(), rendezvous);
		result.addObject("notLinked", notLinked);

		final Collection<User> attendants = rendezvous.getAttendants();
		if (attendants.contains(principal))
			isRSVPing = true;

		result.addObject("isRSVPing", isRSVPing);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		final String momentFormated = formatter.format(rendezvous.getMoment());

		result.addObject("momentFormated", momentFormated);
		result.addObject("rendezvous", rendezvous);

		Collection<Question> questions;

		questions = this.questionService.findAllByRendezvous(rendezvousId);
		result.addObject("question", question);
		result.addObject("questions", questions);
		result.addObject("rendezvousId", rendezvousId);

		final Answer answer = this.answerService.create();
		result.addObject("answer", answer);

		Collection<Answer> answers;
		answers = this.userService.findByPrincipal().getAnswers();
		result.addObject("answers", answers);

		result.addObject("message", message);

		return result;
	}

}
