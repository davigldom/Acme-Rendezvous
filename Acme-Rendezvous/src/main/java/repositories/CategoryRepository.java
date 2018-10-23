
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("select c from Category c where c.parent is not null")
	Collection<Category> findAllChildren();

	@Query("select c from Category c where c.parent.id=?1")
	Collection<Category> findChildren(int categoryId);

	@Query("select c from Category c where c.parent is null")
	Collection<Category> getRoots();

	@Query("select c from Category c where c.name = 'Default'")
	Category findDefaultCategory();

	// B-Level--------------------------------------------------------------------
	// Requirement 11.2.1
	//The average number of categories per rendezvous
	@Query("select sum(case when rq.service.category is not null then 1.0 else 0.0 end)/(select count(r) from Rendezvous r) from Rendezvous r join r.requests rq")
	Double averageNumberCategoriesPerRendezvous();

}
