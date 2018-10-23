
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AnswerService;
import services.QuestionService;
import services.RendezvousService;
import services.UserService;
import domain.Answer;
import domain.Question;
import domain.Rendezvous;

@Controller
@RequestMapping("/question")
public class QuestionController extends AbstractController {

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private QuestionService		questionService;

	@Autowired
	private AnswerService		answerService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private UserService		userService;


	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int rendezvousId, @RequestParam final int userId) {
		ModelAndView result;
		Rendezvous rendezvous;

		rendezvous = this.rendezvousService.findOne(rendezvousId);

		if(!this.userService.isAuthenticated() 
				|| !this.actorService.isAdult()) Assert.isTrue(!rendezvous.isAdultOnly());
		
		Assert.notNull(rendezvous);
		result = new ModelAndView("question/display");

		Collection<Question> questions;

		questions = this.questionService.findAllByRendezvous(rendezvousId);
		result.addObject("questions", questions);
		result.addObject("rendezvousId", rendezvousId);

		Collection<Answer> answers;
		answers = this.answerService.findAllByRendezvousPerUser(rendezvousId, userId);
		result.addObject("answers", answers);

		result.addObject("requestURI", "question/display.do");
		result.addObject("yourAnswers", false);

		return result;
	}

}
