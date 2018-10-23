
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {

	@Query("select m from Manager m where m.userAccount.id=?1")
	Manager findByUserAccountId(int managerId);

	@Query("select m from Manager m, AcmeService s where s member of m.services and s.id=?1")
	Manager findCreator(int serviceId);

	//C-level
	//Requirement 6.2
	@Query("select m from Manager m where m.services.size>(select avg(t.services.size) from Manager t)")
	Collection<Manager> getManagersProvidingMoreServicesThanAverage();

	@Query("select m from Manager m join m.services s where s.cancelled=1 group by m order by count(s) desc")
	Page<Manager> getManagersWithMoreServicesCancelled(Pageable pageable);

}
