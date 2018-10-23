
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.userAccount.id=?1")
	User findByUserAccountId(int userId);

	@Query("select r.attendants from Rendezvous r where r.id=?1")
	Collection<User> findAttendants(int rendezvousId);

	@Query("select u from User u, Answer a where a member of u.answers and a.id=?1")
	User findRespondent(int answerId);

}
