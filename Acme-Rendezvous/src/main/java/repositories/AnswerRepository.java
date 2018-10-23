
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	@Query("select a from Answer a, User u where a member of u.answers and a.question.id=?1 and u.id=?2")
	Answer findAnswerInQuestion(int questionId, int userId);

	@Query("select a from Answer a where a.question.id=?1")
	Collection<Answer> findAllAnswersInQuestion(int questionId);

	@Query("select a from Answer a, User u where a member of u.answers and a.question.id=?1 and u.id=?2")
	Collection<Answer> findAllAnswersInQuestionPerUser(int questionId, int userId);

	@Query("select a from Answer a, User u where a member of u.answers and a.question.rendezvous.id=?1 and u.id=?2")
	Collection<Answer> findAllByRendezvous(int rendezvous, int userId);

}
