
package controllers.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
@RequestMapping("/answer/user")
public class AnswerUserController extends AbstractController {

	@Autowired
	private AnswerService		answerService;

	@Autowired
	private QuestionService		questionService;

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private UserService			userService;

	@Autowired
	private ActorService		actorService;


	public AnswerUserController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@RequestParam final int questionId, Answer answer, final BindingResult binding) {
		ModelAndView result = null;
		final Question question = this.questionService.findOne(questionId);
		final int rendezvousId = question.getRendezvous().getId();
		if(this.rendezvousService.findOne(rendezvousId).isAdultOnly())Assert.isTrue(this.actorService.isAdult());
		answer.setQuestion(question);
		answer = this.answerService.reconstruct(answer, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(answer, rendezvousId);
		else
			try {
				this.answerService.save(answer);
				result = new ModelAndView("redirect:/rendezvous/display.do?rendezvousId=" + rendezvousId);
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(answer, rendezvousId, "question.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "saveRSVP")
	public ModelAndView saveRSVP(@RequestParam final int questionId, Answer answer, final BindingResult binding) {
		ModelAndView result = null;
		final Question question = this.questionService.findOne(questionId);
		final int rendezvousId = question.getRendezvous().getId();
		if(this.rendezvousService.findOne(rendezvousId).isAdultOnly())Assert.isTrue(this.actorService.isAdult());
		boolean isAllAnswered = false;
		answer.setQuestion(question);
		answer = this.answerService.reconstruct(answer, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView2(answer, rendezvousId);
		else
			try {
				this.answerService.saveRSVP(answer, question);

				final List<Question> questions = new ArrayList<Question>(this.questionService.findAllByRendezvous(rendezvousId));
				final List<Answer> answers = new ArrayList<Answer>(this.answerService.findAllByRendezvous(rendezvousId));
				if (answers.size() == questions.size())
					isAllAnswered = true;

				if (isAllAnswered == true) {
					this.rendezvousService.RSVP(rendezvousId);
					result = new ModelAndView("redirect:/rendezvous/user/list.do");
				} else if (isAllAnswered == false)
					result = new ModelAndView("redirect:/question/user/display.do?rendezvousId=" + rendezvousId);
			} catch (final Throwable oops) {
				result = this.createEditModelAndView2(answer, rendezvousId, "question.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Answer answer, final int rendezvousId) {
		ModelAndView result;

		result = this.createEditModelAndView(answer, rendezvousId, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Answer answer, final int rendezvousId, final String message) {
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

		final Question question = this.questionService.create(rendezvousId);
		Collection<Question> questions;

		questions = this.questionService.findAllByRendezvous(rendezvousId);
		result.addObject("question", question);
		result.addObject("questions", questions);
		result.addObject("rendezvousId", rendezvousId);

		result.addObject("answer", answer);

		Collection<Answer> answers;
		answers = this.userService.findByPrincipal().getAnswers();
		result.addObject("answers", answers);

		result.addObject("message", message);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final Answer answer, final int rendezvousId) {
		ModelAndView result;

		result = this.createEditModelAndView2(answer, rendezvousId, null);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final Answer answer, final int rendezvousId, final String message) {
		ModelAndView result;
		Rendezvous rendezvous;

		rendezvous = this.rendezvousService.findOne(rendezvousId);

		Assert.notNull(rendezvous);
		result = new ModelAndView("question/display");

		final Question question = this.questionService.create(rendezvousId);
		Collection<Question> questions;

		questions = this.questionService.findAllByRendezvous(rendezvousId);
		result.addObject("question", question);
		result.addObject("questions", questions);
		result.addObject("rendezvousId", rendezvousId);

		result.addObject("answer", answer);

		Collection<Answer> answers;
		answers = this.userService.findByPrincipal().getAnswers();
		result.addObject("answers", answers);

		result.addObject("requestURI", "/question/user/display.do?rendezvousId=" + rendezvousId);
		result.addObject("yourAnswers", true);

		result.addObject("message", message);

		return result;
	}
}
