
package services;

import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AnswerRepository;
import domain.Answer;
import domain.Question;
import domain.Rendezvous;
import domain.User;

@Service
@Transactional
public class AnswerService {

	@Autowired
	private AnswerRepository	answerRepository;

	@Autowired
	private UserService			userService;

	@Autowired
	private ActorService		actorService;


	public Answer create() {
		final Answer result;

		result = new Answer();

		return result;
	}

	public Answer findOne(final int answerId) {
		Answer result;
		Assert.isTrue(answerId != 0);
		result = this.answerRepository.findOne(answerId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Answer> findAll() {
		return this.answerRepository.findAll();
	}

	public Answer saveRSVP(final Answer answer, final Question question) {
		Answer result;
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(!this.isAnswered(question));
		Assert.notNull(answer);
		final Rendezvous rendezvous = answer.getQuestion().getRendezvous();

		answer.setQuestion(question);
		
		if(!this.actorService.isAdult()) Assert.isTrue(!rendezvous.isAdultOnly());
		Assert.isTrue(rendezvous.getMoment().after(new Date()));
		

		result = this.answerRepository.save(answer);

		question.getAnswers().add(result);
		principal.getAnswers().add(result);

		return result;
	}

	public Answer save(final Answer answer) {
		Answer result;
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(!this.isAnswered(answer.getQuestion()));
		Assert.notNull(answer);

		final Rendezvous rendezvous = answer.getQuestion().getRendezvous();
		Assert.isTrue(rendezvous.getAttendants().contains(principal));

		if(!this.actorService.isAdult()) Assert.isTrue(!rendezvous.isAdultOnly());
		Assert.isTrue(rendezvous.getMoment().after(new Date()));
		
		
		result = this.answerRepository.save(answer);

		answer.getQuestion().getAnswers().add(result);
		principal.getAnswers().add(result);

		return result;
	}

	public void delete(final Answer answer) {
		Assert.notNull(answer);

		//		final Question question = answer.getQuestion();
		//		question.getAnswers().remove(answer);

		final User user = this.userService.findRespondent(answer.getId());
		user.getAnswers().remove(answer);

		this.answerRepository.delete(answer);
	}

	public void deleteCancel(final Answer answer) {
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(answer);
		Assert.notNull(principal);
		Assert.isTrue(principal.getAnswers().contains(answer));

		final Question question = answer.getQuestion();
		question.getAnswers().remove(answer);

		principal.getAnswers().remove(answer);

		this.answerRepository.delete(answer);
	}

	// Check if the principal has answered the question
	public boolean isAnswered(final Question question) {
		boolean result = true;
		final User principal = this.userService.findByPrincipal();
		Answer answer;

		answer = this.answerRepository.findAnswerInQuestion(question.getId(), principal.getId());
		if (answer == null)
			result = false;

		return result;

	}

	public Collection<Answer> findAllAnswersInQuestion(final int questionId) {
		Collection<Answer> result;

		result = this.answerRepository.findAllAnswersInQuestion(questionId);
		Assert.notNull(result);

		return result;
	}

	public Collection<Answer> findAllAnswersInQuestionPerUser(final int questionId) {
		Collection<Answer> result;
		final User principal = this.userService.findByPrincipal();

		result = this.answerRepository.findAllAnswersInQuestionPerUser(questionId, principal.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Answer> findAllByRendezvous(final int rendezvousId) {
		Collection<Answer> result;
		final User principal = this.userService.findByPrincipal();

		result = this.answerRepository.findAllByRendezvous(rendezvousId, principal.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Answer> findAllByRendezvousPerUser(final int rendezvousId, final int userId) {
		Collection<Answer> result;

		result = this.answerRepository.findAllByRendezvous(rendezvousId, userId);
		Assert.notNull(result);

		return result;
	}

	public void flush(){
		this.answerRepository.flush();
	}
	
	//Reconstruct

	@Autowired
	private Validator	validator;


	public Answer reconstruct(final Answer answer, final BindingResult binding) {
		Answer answerStored;

		if (answer.getId() != 0) {
			answerStored = this.answerRepository.findOne(answer.getId());
			//result.setResponse(answer.getResponse());
			answer.setQuestion(answerStored.getQuestion());
			answer.setId(answerStored.getId());
			answer.setVersion(answerStored.getVersion());
		}
		this.validator.validate(answer, binding);
		return answer;

	}
}
