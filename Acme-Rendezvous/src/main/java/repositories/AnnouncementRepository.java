
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

	@Query("select a from Announcement a where a.rendezvous.id=?1")
	Collection<Announcement> findAllByRendezvous(int rendezvousId);

	@Query("select r.announcements from Rendezvous r join r.attendants u where u.id=?1")
	Collection<Announcement> findAllByRsvpRendezvouses(int userId);

	// Requirement 17.2.1
	// The average and the standard deviation of announcements per rendezvous
	@Query("select avg(r.announcements.size) from Rendezvous r")
	Double averageAnnouncementsPerRendezvous();

	@Query("select sqrt(sum(r.announcements.size*r.announcements.size)/count(r.announcements.size)-(avg(r.announcements.size)*avg(r.announcements.size))) from Rendezvous r")
	Double standardDeviationAnnouncementsPerRendezvous();

}
