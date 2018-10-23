
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.AcmeService;

@Repository
public interface AcmeServiceRepository extends JpaRepository<AcmeService, Integer> {

	@Query("select s from AcmeService s, Manager m where s member of m.services and m.id=?1")
	Collection<AcmeService> findByPrincipal(int principalId);

	// B-Level--------------------------------------------------------------------

	// Requirement 11.2.2
	// The average ratio of services in each category
	@Query("select avg(c.services.size) from Category c")
	Double averageRatioServicesPerCategory();

	// Requirement 11.2.3
	// Average,minimum,maximum and standard deviation of services requested per rendezvous
	@Query("select avg(r.requests.size) from Rendezvous r")
	Double averageServicesRequestedPerRendezvous();

	@Query("select min(r.requests.size) from Rendezvous r")
	Integer minServicesRequestedPerRendezvous();

	@Query("select max(r.requests.size) from Rendezvous r")
	Integer maxServicesRequestedPerRendezvous();

	@Query("select sqrt(sum(r.requests.size*r.requests.size)/count(r.requests.size)-(avg(r.requests.size)*(r.requests.size))) from Rendezvous r")
	Double standardDeviationServicesRequestedPerRendezvous();

	// Requirement 11.2.4
	// Top selling services
	@Query("select s from AcmeService s order by s.requests.size desc")
	Page<AcmeService> getTop3Selling(Pageable pageable);

}
