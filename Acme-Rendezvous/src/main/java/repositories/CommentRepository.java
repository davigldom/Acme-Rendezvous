
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

	@Query("select c from Comment c where c.rendezvous.id=?1")
	Collection<Comment> findAllByRendezvous(int rendezvousId);

	@Query("select distinct c.replies from Comment c")
	Collection<Comment> findAllReplies();

	@Query("select c from Comment c join c.replies r where r.id=?1")
	Comment findRootComment(int replyId);
}
