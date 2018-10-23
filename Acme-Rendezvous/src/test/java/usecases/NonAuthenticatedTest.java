
package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ManagerService;
import services.RendezvousService;
import services.UserService;
import utilities.AbstractTest;
import domain.Answer;
import domain.Manager;
import domain.Question;
import domain.Rendezvous;
import domain.Request;
import domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class NonAuthenticatedTest extends AbstractTest {

	// System under test
	@Autowired
	private RendezvousService	rendezvousService;
	@Autowired
	private UserService			userService;
	@Autowired
	private ManagerService		managerService;


	@Before
	public void setupAuthentication() {
		SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
	}

	// Tests ------------------------------------------------------------------

	// Acme-Rendezvous 1.0 FR01.1:
	// UNAUTHENTICATED - Register as an user *****************************************************************************************************************
	// POSITIVE: Creating a user
	@Test
	public void registerAsUser() {
		final User user = this.userService.create();
		user.getUserAccount().setUsername("testUser");
		user.getUserAccount().setPassword("testUser");
		user.setBirthdate(new Date(System.currentTimeMillis() - 1000));
		user.setEmail("user@gmail.co");
		user.setName("user");
		user.setPhone("+34664052167");
		user.setPostalAddress("41530");
		user.setSurname("surname");

		this.userService.save(user);

		//Login as new user
		this.authenticate("testUser");
		this.authenticate(null);
	}

	// NEGATIVE: Creating a user with a username that already exists
	@Test(expected = DataIntegrityViolationException.class)
	public void registerAsUserWhoseUsernameExists() {
		final User user = this.userService.create();
		user.getUserAccount().setUsername("user1");
		user.getUserAccount().setPassword("testUser");
		user.setBirthdate(new Date());
		user.setEmail("user@gmail.co");
		user.setName("user");
		user.setPhone("+34664052167");
		user.setPostalAddress("41530");
		user.setSurname("surname");

		this.userService.save(user);
		this.userService.flush();
	}
	//************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR01.2:
	// UNAUTHENTICATED - List users *****************************************************************************************************************
	@Test
	public void driverListUsers() {
		final Object testingData[][] = {
			// POSITIVE - as admin shouldn't fail the test
			{
				"admin", null
			},
			// POSITIVE - as user1 shouldn't fail the test
			{
				"user1", null
			},
			// POSITIVE - as manager1 shouldn't fail the test
			{
				"manager1", null
			},
			// POSITIVE - Unauthenticated shouldn't fail the test
			{
				null, null
			},
			// NEGATIVE - as nonexistent should fail the test
			{
				"user213894", IllegalArgumentException.class
			},
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateListUsers((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateListUsers(final String username, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			this.userService.findAll();
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	//************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR01.2:
	// UNAUTHENTICATED - Display user *****************************************************************************************************************
	//POSITIVE test
	@Test
	public void navigateToUserProfilePositiveTest() {
		final int userId = this.getEntityId("user1");
		this.authenticate(null);
		this.userService.findOne(userId);
		
		//Display RSVPd rendezvouses from the user
		this.rendezvousService.findRSVPdRendezvousesPerUser(userId);
		this.unauthenticate();
	}
	//NEGATIVE test: should fail because the user does not exist
	@Test(expected = AssertionError.class)
	public void navigateToUserProfileNegativeTest() {
		final int userId = this.getEntityId("user99");
		this.authenticate(null);
		this.userService.findOne(userId);
		this.rendezvousService.findRSVPdRendezvousesPerUser(userId);
		this.unauthenticate();
	}
	//************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR01.3:
	// UNAUTHENTICATED - List all rendezvouses *****************************************************************************************************************
	//POSITIVE
	@Test
	public void listRendezvousesPositiveTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
		
		//The fixture contains 7 rendezvouses
		Assert.isTrue(rendezvouses.size() == 7);

		super.unauthenticate();
	}

	//NEGATIVE
	@Test(expected = IllegalArgumentException.class)
	public void listRendezvousesNegativeTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
		rendezvouses.remove(0);
		Assert.isTrue(rendezvouses.size() == 7);

		super.unauthenticate();
	}
	//************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR01.3:
	// UNAUTHENTICATED - Display rendezvous *****************************************************************************************************************
	//POSITIVE
	@Test
	public void displayRendezvousesPositiveTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());

		final int rendezvousId = super.getEntityId("rendezvous1");
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvouses.contains(rendezvous));

		super.unauthenticate();
	}

	// NEGATIVE - Display a nonexistent rendezvous
	@Test(expected = AssertionError.class)
	public void displayRendezvousesNegativeTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());

		final int rendezvousId = super.getEntityId("rendezvous123");
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvouses.contains(rendezvous));

		super.unauthenticate();
	}
	
	//Display creator and attendants: See test cases for FR02
	//************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR15.1:
	// UNAUTHENTICATED - List of announcements associated to a rendezvous *****************************************************************************************************************
	//Positive test
	@Test
	public void ListAnnoucementsPositiveTest() {
		final int rendezvousId = this.getEntityId("rendezvous1");
		this.authenticate(null);
		final Rendezvous rendezvous1 = this.rendezvousService.findOne(rendezvousId);
		
		//In our fixture, rendezvous1 has announcements
		Assert.isTrue(!rendezvous1.getAnnouncements().isEmpty());
		this.unauthenticate();
	}

	//Negative test: should fail because the rendezvous does not exist
	@Test(expected = AssertionError.class)
	public void ListAnnouncementsNegativeTest() {
		final int rendezvousId = this.getEntityId("rendezvous99");
		this.authenticate(null);
		final Rendezvous rendezvous99 = this.rendezvousService.findOne(rendezvousId);
		rendezvous99.getAnnouncements();
		this.unauthenticate();
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR15.2:
	// UNAUTHENTICATED - Navigate to similar rendezvouses *****************************************************************************************************************
	// POSITIVE
	@Test
	public void navigateToSimilarRendezvousesPositiveTest() {
		final Rendezvous main = this.rendezvousService.findOne(super.getEntityId("rendezvous1"));
		final List<Rendezvous> linked = new ArrayList<Rendezvous>(this.rendezvousService.findLinked(main.getId()));
		Assert.notNull(this.rendezvousService.findOne(linked.get(0).getId()));
	}

	// NEGATIVE - This test should fail as the rendezvous that we tried to get is not linked to the main one
	@Test(expected = IllegalArgumentException.class)
	public void navigateToSimilarRendezvousesNegativeTest() {
		final Rendezvous main = this.rendezvousService.findOne(super.getEntityId("rendezvous1"));
		final List<Rendezvous> linked = new ArrayList<Rendezvous>(this.rendezvousService.findLinked(main.getId()));
		Assert.isTrue(linked.contains(this.rendezvousService.findOne(super.getEntityId("rendezvous6"))));
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR20.1:
	// UNAUTHENTICATED - List of attendants *****************************************************************************************************************
	// POSITIVE
	@Test
	public void listRendezvousAttendantsPositiveTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
		Assert.isTrue(rendezvouses.size() == 7);

		final int rendezvousId = super.getEntityId("rendezvous1");
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvouses.contains(rendezvous));
		final User creator = rendezvous.getCreator();
		Assert.isTrue(creator.getName() != null);

		//In our fixture, rendezvous1 has 3 attendants
		final List<User> attendants = new ArrayList<User>(rendezvous.getAttendants());
		Assert.isTrue(attendants.size() == 3);

		super.unauthenticate();
	}

	// NEGATIVE - Incorrect list of attendants (missing one)
	@Test(expected = IllegalArgumentException.class)
	public void listRendezvousAttendantsNegativeTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
		Assert.isTrue(rendezvouses.size() == 7);

		final int rendezvousId = super.getEntityId("rendezvous1");
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvouses.contains(rendezvous));
		final User creator = rendezvous.getCreator();
		Assert.isTrue(creator.getName() != null);

		final List<User> attendants = new ArrayList<User>(rendezvous.getAttendants());
		attendants.remove(0);
		Assert.isTrue(attendants.size() == 3);

		super.unauthenticate();
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR20.1:
	// UNAUTHENTICATED - Obtain the answers of an attendant *****************************************************************************************************************
	// POSITIVE
	@Test
	public void obtainAnswersPositiveTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
		Assert.isTrue(rendezvouses.size() == 7);

		final int rendezvousId = super.getEntityId("rendezvous1");
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvouses.contains(rendezvous));

		final List<Question> questions = new ArrayList<Question>(rendezvous.getQuestions());
		Assert.isTrue(questions.size() == 6);

		final List<User> attendants = new ArrayList<User>(rendezvous.getAttendants());
		Assert.isTrue(attendants.size() == 3);

		final int attendantId = super.getEntityId("user1");
		final User attendant = this.userService.findOne(attendantId);
		Assert.isTrue(attendants.contains(attendant));

		
		//In our fixture, user1 has answered the 6 questions for rendezvous1
		final List<Answer> answers = new ArrayList<Answer>(attendant.getAnswers());
		Assert.isTrue(answers.size() == 6);
		Assert.isTrue(questions.contains(answers.get(0).getQuestion()));

		super.unauthenticate();
	}

	// NEGATIVE - If you try to see the answers of an user who doesn't attend the rendezvous, the test should fail
	@Test(expected = IllegalArgumentException.class)
	public void obtainAnswersNegativeTest() {
		super.authenticate(null);

		final List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
		Assert.isTrue(rendezvouses.size() == 6);

		final int rendezvousId = super.getEntityId("rendezvous1");
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		Assert.isTrue(rendezvouses.contains(rendezvous));

		final List<Question> questions = new ArrayList<Question>(rendezvous.getQuestions());
		Assert.isTrue(questions.size() == 6);

		final List<User> attendants = new ArrayList<User>(rendezvous.getAttendants());
		Assert.isTrue(attendants.size() == 2);

		final int attendantId = super.getEntityId("user3");
		final User attendant = this.userService.findOne(attendantId);
		Assert.isTrue(attendants.contains(attendant));

		final List<Answer> answers = new ArrayList<Answer>(attendant.getAnswers());
		Assert.isTrue(answers.size() == 6);
		Assert.isTrue(questions.contains(answers.get(0).getQuestion()));

		super.unauthenticate();
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR03.1:
	// UNAUTHENTICATED - Register to the system as a manager ****************************************************************************************************************
	// POSITIVE - Creating a manager
	@Test
	public void registerAsManager() {
		final Manager manager = this.managerService.create();
		manager.getUserAccount().setUsername("testManager");
		manager.getUserAccount().setPassword("testManager");
		manager.setBirthdate(new Date(System.currentTimeMillis() - 1000));
		manager.setEmail("manager@gmail.co");
		manager.setName("manager");
		manager.setPhone("+34664052167");
		manager.setPostalAddress("41530");
		manager.setSurname("surname");
		manager.setVat("N12344672F");

		this.managerService.save(manager);

		
		//Login as new manager
		this.authenticate("testManager");
		this.authenticate(null);
	}

	// Negative test: Creating a manager with a username that already exists
	@Test(expected = DataIntegrityViolationException.class)
	public void registerAsManagerWhoseUsernameExists() {
		final Manager manager = this.managerService.create();
		manager.getUserAccount().setUsername("manager1");
		manager.getUserAccount().setPassword("testManager");
		manager.setBirthdate(new Date());
		manager.setEmail("manager@gmail.co");
		manager.setName("manager");
		manager.setPhone("+34664052167");
		manager.setPostalAddress("41530");
		manager.setSurname("surname");
		manager.setVat("A12379447F");

		this.managerService.save(manager);
		this.managerService.flush();
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR10.1:
	// UNAUTHENTICATED - List the rendezvouses in the system grouped by category ****************************************************************************************************************
	//POSITIVE
	@Test
	public void listByCategory() {
		final Collection<Rendezvous> rendezvouses = this.rendezvousService.findByCategory(this.getEntityId("category3"));
		boolean works = true;
		
		//Make sure every rendezvous has at least one request for a service that belongs to category3
		for (final Rendezvous rendezvous : rendezvouses)
			for (final Request r : rendezvous.getRequests()) {
				if (r.getService().getCategory().getId() == this.getEntityId("category3"))
					break;
				works = false;
			}

		Assert.isTrue(works);

	}

	//NEGATIVE - We try to find services that belong to a nonexistent category.
	@Test(expected = AssertionError.class)
	public void listByCategoryNonExistentUser() {
		final Collection<Rendezvous> rendezvouses = this.rendezvousService.findByCategory(this.getEntityId("category123"));
		for (final Rendezvous rendezvous : rendezvouses)
			for (final Request r : rendezvous.getRequests())
				Assert.isTrue(r.getService().getCategory().getId() == this.getEntityId("category1"));
	}
	// ************************************************************************************************************************************************************

}
