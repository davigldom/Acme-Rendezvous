
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.QuestionRepository;
import domain.Answer;
import domain.Question;
import domain.Rendezvous;
import domain.User;

@Service
@Transactional
public class QuestionService {

	@Autowired
	private QuestionRepository	questionRepository;

	@Autowired
	private UserService			userService;

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private AnswerService		answerService;


	public Question create(final int rendezvousId) {
		final Question result;

		result = new Question();

		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		result.setRendezvous(rendezvous);

		return result;
	}
	public Question findOne(final int questionId) {
		Question result;
		Assert.isTrue(questionId != 0);
		result = this.questionRepository.findOne(questionId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Question> findAll() {
		return this.questionRepository.findAll();
	}

	public Question save(final Question question) {
		Question result = null;
		final User principal = this.userService.findByPrincipal();

		Assert.notNull(question);
		Assert.isTrue(question.getRendezvous().getCreator().equals(principal));

		result = this.questionRepository.save(question);

		return result;
	}

	public void delete(final Question question) {
		Assert.notNull(question);

		final java.util.Iterator<Answer> iter = question.getAnswers().iterator();
		while (iter.hasNext()) {
			final Answer answer = iter.next();
			iter.remove();
			this.answerService.delete(answer);

		}

		this.questionRepository.delete(question);
	}
	public Question findOneToEdit(final int questionId) {
		Question result;
		final User principal = this.userService.findByPrincipal();

		Assert.isTrue(questionId != 0);
		result = this.questionRepository.findOne(questionId);
		Assert.notNull(result);
		Assert.isTrue(result.getCreator().equals(principal));
		return result;
	}

	public Collection<Question> findAllByRendezvous(final int rendezvousId) {
		Collection<Question> result;

		result = this.questionRepository.findAllByRendezvous(rendezvousId);
		Assert.notNull(result);

		return result;
	}

	public void flush() {
		this.questionRepository.flush();
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public Question reconstruct(final Question question, final BindingResult binding) {
		Question questionStored;

		if (question.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			final Collection<Answer> answers = new ArrayList<Answer>();

			question.setCreator(principal);
			question.setAnswers(answers);
		} else {
			questionStored = this.questionRepository.findOne(question.getId());
			question.setAnswers(questionStored.getAnswers());
			question.setCreator(questionStored.getCreator());
			question.setId(questionStored.getId());
			question.setRendezvous(questionStored.getRendezvous());
			question.setVersion(questionStored.getVersion());
		}
		this.validator.validate(question, binding);
		return question;

	}

}
