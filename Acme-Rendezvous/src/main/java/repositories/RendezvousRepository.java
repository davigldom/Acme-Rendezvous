
package repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Rendezvous;

@Repository
public interface RendezvousRepository extends JpaRepository<Rendezvous, Integer> {

	@Query("select r from Rendezvous r, User u where u member of r.attendants and u.id=?1")
	Collection<Rendezvous> findRSVPdRendezvouses(int userId);

	@Query("select r from Rendezvous r, User u where u member of r.attendants and u.id=?1 and r.adultOnly=0")
	Collection<Rendezvous> findRSVPdRendezvousesNoAdult(int userId);

	@Query("select r from Rendezvous r, User u where u member of r.attendants and u.id=?1")
	Collection<Rendezvous> findRSVPdRendezvousesPerUser(int userId);

	@Query("select r from Rendezvous r where r.creator.id=?1")
	Collection<Rendezvous> findAllByCreator(int userId);

	@Query("select r from Rendezvous r where r.creator.id=?1 and r.adultOnly=0")
	Collection<Rendezvous> findAllByCreatorNoAdult(int userId);

	@Query("select r from Rendezvous r where r.creator.id=?1 and r.id!=?2 and r.deleted=0")
	Collection<Rendezvous> findAllAvailableToLinkByCreator(int userId, int rendezvousId);

	@Query("select r from Rendezvous r where r.adultOnly=0")
	Collection<Rendezvous> findAllNoAdult();

	@Query("select distinct r from Rendezvous r join r.requests rq where r.adultOnly=0 and rq.service in (select service from Category c join c.services service where c.id =?1)")
	Collection<Rendezvous> findNoAdultByCategory(int categoryId);

	@Query("select distinct r from Rendezvous r join r.requests rq where rq.service in (select service from Category c join c.services service where c.id =?1)")
	Collection<Rendezvous> findByCategory(int categoryId);

	@Query("select r from Rendezvous r join r.requests rq where rq.id=?1")
	Rendezvous findByRequest(int requestId);

	@Query("select r from Rendezvous r, Request t where t member of r.requests and t.service.id=?1 and r.creator.id=?2")
	Collection<Rendezvous> findWithService(int serviceId, int principalId);

	// C-Level--------------------------------------------------------------------
	// Requirement 6.3.1
	// The average and the standard deviation of rendezvouses created per user
	@Query("select avg(u.rendezvouses.size) from User u")
	Double averageRendezvousesPerUser();

	@Query("select sqrt(sum(u.rendezvouses.size*u.rendezvouses.size)/count(u.rendezvouses.size)-(avg(u.rendezvouses.size)*avg(u.rendezvouses.size))) from User u")
	Double standardDeviationRendezvousesPerUser();

	// Requirement 6.3.2
	// The ratio of users who have ever created a rendezvous versus the users who have never created any rendezvouses
	@Query("select distinct 1.0*(select count(u) from User u where u.rendezvouses.size>0)/(select count(u) from User u where u.rendezvouses.size=0) from User u")
	Double everCreateRendezvous();
	// Requirement 6.3.3
	// The average and the standard deviation of users per rendezvous
	@Query("select avg(r.attendants.size) from Rendezvous r")
	Double averageUsersPerRendezvous();

	@Query("select sqrt(sum(r.attendants.size*r.attendants.size)/count(r.attendants.size)-(avg(r.attendants.size)*avg(r.attendants.size))) from Rendezvous r")
	Double standardDeviationUsersPerRendezvous();

	// Requirement 6.3.4
	// The average and the standard deviation of rendezvouses that are RSVPd per user
	@Query("select 1.0*(select count(r) from Rendezvous r where u member of r.attendants)/count(u) from User u")
	Double averageRSVPdRendezvousesPerUser();

	@Query("select stddev(1.0*(select count(r) from Rendezvous r where u member of r.attendants)) from User u")
	Double standardDeviationRSVPdRendezvousesPerUser();

	// Requirement 6.3.5
	// The top-10 rendezvouses in terms of users who have RSVPd them.
	@Query("select r from Rendezvous r order by r.attendants.size desc")
	List<Rendezvous> top10RSVPdRendezvouses(Pageable pageable);

	//Requirement 17.2.2
	//The rendezvouses that whose number of announcements is above 75% the average number of announcements per rendezvous.
	@Query("select r from Rendezvous r where r.announcements.size > (select avg(r.announcements.size) from Rendezvous r)*0.75")
	Collection<Rendezvous> selectAbove75PercentAnnouncements();

	//Requirement 17.2.3
	//The rendezvouses that are linked to a number of rendezvouses that is greater than the average plus 10%.
	@Query("select r from Rendezvous r where r.linked.size > (select avg(r.linked.size) from Rendezvous r)*1.10")
	Collection<Rendezvous> selectAboveTenPercentPlusAverageAnnouncements();

	//Requirement 22.1.1
	//The average and the standard deviation of the number of questions per rendezvous
	@Query("select avg(r.questions.size) from Rendezvous r")
	Double averageQuestionsPerRendezvous();

	@Query("select sqrt(sum(r.questions.size*r.questions.size)/count(r.questions.size)-(avg(r.questions.size)*avg(r.questions.size))) from Rendezvous r")
	Double standardDeviationQuestionsPerRendezvous();

	//Requirement 22.1.2
	//The average and the standard deviation of the number of answers to questions per rendezvous
	@Query("select 1.0*sum(q.answers.size)/(select count(r) from Rendezvous r) from Rendezvous r join r.questions q")
	Double averageAnswersToQuestionsPerRendezvous();

	@Query("select sqrt(sum(q.answers.size*q.answers.size)/count(q.answers.size)-(avg(q.answers.size)*avg(q.answers.size))) from Question q")
	Double standardDeviationAnswersToQuestionsPerRendezvous();

	//Requirement 22.1.3
	// The average and the standard deviation of replies per comment.
	@Query("select avg(c.replies.size) from Comment c")
	Double averageRepliesPerComment();

	@Query("select sqrt(sum(c.replies.size*c.replies.size)/count(c.replies.size)-(avg(c.replies.size)*avg(c.replies.size))) from Comment c")
	Double standardDeviationRepliesPerComment();
}
