
package usecases;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AcmeServiceService;
import services.CategoryService;
import services.ManagerService;
import utilities.AbstractTest;
import domain.AcmeService;
import domain.Manager;
import domain.Request;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ManagerTest extends AbstractTest {

	// System under test

	@Autowired
	private ManagerService		managerService;

	@Autowired
	private AcmeServiceService	acmeServiceService;

	@Autowired
	private CategoryService		categoryService;


	// Acme-Rendezvous 2.0 Extra Funtional requirement:
	//MANAGER - EDIT MANAGER **************************************************************************************************************
	// POSITIVE: Editing a manager
	@Test
	public void editManagerData() {
		this.authenticate("manager1");
		final Manager manager = this.managerService.findOneToEdit(this.getEntityId("manager1"));
		manager.setName("Test name");
		final Manager result = this.managerService.save(manager);

		//Make sure the manager has been saved
		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}

	// Acme-Rendezvous 2.0 Extra Funtional requirement:
	// NEGATIVE: Editing other manager's personal data
	@Test(expected = IllegalArgumentException.class)
	public void editOtherManagerData() {
		this.authenticate("manager1");
		final Manager manager = this.managerService.findOneToEdit(this.getEntityId("manager2"));
		manager.setName("Test name");
		final Manager result = this.managerService.save(manager);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}
	// ***************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 Extra Funtional requirement:
	//MANAGER - DELETE MANAGER *****************************************************************************************************************************
	// POSITIVE: Deleting a manager
	@Test
	public void deleteManagerData() {
		this.authenticate("manager1");
		final Manager manager = this.managerService.findOneToEdit(this.getEntityId("manager1"));
		this.managerService.delete(manager);

		//Make sure the manager has been deleted
		Assert.isTrue(!this.managerService.findAll().contains(manager));

		this.authenticate(null);
	}

	// NEGATIVE: Deleting other manager
	@Test(expected = IllegalArgumentException.class)
	public void deleteOtherManager() {
		this.authenticate("manager1");
		final Manager manager = this.managerService.findOne(this.getEntityId("manager2"));
		this.managerService.delete(manager);

		Assert.isTrue(!this.managerService.findAll().contains(manager));

		this.authenticate(null);
	}
	// ***************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR05.1:
	// MANAGER & USER - LIST ALL THE SERVICES IN THE SYSTEM ****************************************************************************************************
	@Test
	public void driverListServices() {
		final Object testingData[][] = {
			// POSITIVE - User
			{
				"user1", null
			},
			// POSITIVE - manager
			{
				"manager1", null
			},
			// NEGATIVE - Unauthenticated actor can't see services
			{
				null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateListServices((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateListServices(final String username, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final Collection<AcmeService> services = this.acmeServiceService.findAll();
			//The fixture contains 4 services
			Assert.isTrue(services.size() == 4);
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ***************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR05.2:
	// MANAGER - List my services ***********************************************************************************************************************
	@Test
	public void driverListMyServices() {
		final Object testingData[][] = {
			// POSITIVE - As manager shouldn't fail the test
			{
				"manager1", null
			},
			// NEGATIVE - Unauthenticated should fail the test
			{
				null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateListMyServices((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateListMyServices(final String username, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final Collection<AcmeService> services = this.acmeServiceService.findByPrincipal();

			//In our fixture, manager1 has created 3 services
			if (username.equals("manager1"))
				Assert.isTrue(services.size() == 3);
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ***************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR05.2:
	// MANAGER - Create service
	@Test
	public void driverCreateService() {
		final Object testingData[][] = {
			// POSITIVE - As manager shouldn't fail the test
			{
				"manager1", null
			},
			// NEGATIVE - Unauthenticated should fail the test
			{
				null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateCreateService((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateCreateService(final String username, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			final AcmeService acmeService = new AcmeService();

			acmeService.setName("Test Service Name");
			acmeService.setDescription("Test Service Description");
			acmeService.setPrice(5.0);
			acmeService.setCategory(this.categoryService.findDefaultCategory());
			acmeService.setRequests(new ArrayList<Request>());
			acmeService.setCancelled(false);

			this.authenticate(username);
			final AcmeService result = this.acmeServiceService.save(acmeService);
			this.acmeServiceService.flush();

			//Make sure the service has been created
			Assert.isTrue(this.acmeServiceService.findAll().contains(result));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ***************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR05.2:
	// MANAGER - Update service **************************************************************************************************************************
	@Test
	public void driverUpdateService() {
		final Object testingData[][] = {
			// POSITIVE - As manager shouldn't fail the test
			{
				"manager1", "acmeService1", null
			},
			// NEGATIVE - Unauthenticated should fail the test
			{
				null, "acmeService1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateUpdateService((String) testingData[i][0], super.getEntityId((String) testingData[i][1]), (Class<?>) testingData[i][2]);
	}

	protected void templateUpdateService(final String username, final int beanId, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final AcmeService acmeService = this.acmeServiceService.findOneToEdit(beanId);

			acmeService.setName("Updating");
			acmeService.setDescription("Updating Service test description");
			acmeService.setPrice(10.0);

			final AcmeService result = this.acmeServiceService.save(acmeService);

			//Make sure the service has been updated
			Assert.isTrue(result.getName().equals("Updating"));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ***************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR05.2:
	// MANAGER - Delete service *************************************************************************************************************************
	@Test
	public void driverDeleteService() {
		final Object testingData[][] = {
			// POSITIVE - As manager shouldn't fail the test
			{
				"manager1", "acmeService4", null
			},
			// NEGATIVE - Unauthenticated should fail the test
			{
				null, "acmeService4", IllegalArgumentException.class
			},
			// NEGATIVE - Requested service should fail the test
			{
				"manager1", "acmeService1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteService((String) testingData[i][0], super.getEntityId((String) testingData[i][1]), (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteService(final String username, final int beanId, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final AcmeService acmeService = this.acmeServiceService.findOneToEdit(beanId);

			this.acmeServiceService.delete(acmeService);

			//Make sure the service has been deleted
			Assert.isTrue(!this.acmeServiceService.findAll().contains(acmeService));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ***************************************************************************************************************************************************

}
