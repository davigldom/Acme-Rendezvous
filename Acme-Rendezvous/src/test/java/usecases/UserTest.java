
package usecases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AcmeServiceService;
import services.AnnouncementService;
import services.AnswerService;
import services.CommentService;
import services.QuestionService;
import services.RendezvousService;
import services.RequestService;
import services.UserService;
import utilities.AbstractTest;
import domain.AcmeService;
import domain.Announcement;
import domain.Answer;
import domain.Comment;
import domain.Question;
import domain.Rendezvous;
import domain.Request;
import domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class UserTest extends AbstractTest {

	// System under test
	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private UserService			userService;

	@Autowired
	private AcmeServiceService	acmeServiceService;

	@Autowired
	private RequestService		requestService;

	@Autowired
	private CommentService		commentService;

	@Autowired
	private AnnouncementService	announcementService;

	@Autowired
	private QuestionService		questionService;

	@Autowired
	private AnswerService		answerService;


	// Acme-Rendezvous 1.0 Extra Funtional requirement:
	// USER - Edit user *************************************************************************************************************************************
	// POSITIVE - Editing a user
	@Test
	public void editUserData() {
		this.authenticate("user1");
		final User user = this.userService.findOneToEdit(this.getEntityId("user1"));
		user.setName("Test name");
		final User result = this.userService.save(user);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}

	// NEGATIVE - Editing other user's personal data
	@Test(expected = IllegalArgumentException.class)
	public void editOtherUserData() {
		this.authenticate("user1");
		final User user = this.userService.findOneToEdit(this.getEntityId("user2"));
		user.setName("Test name");
		final User result = this.userService.save(user);

		Assert.isTrue(result.getName().equals("Test name"));

		this.authenticate(null);
	}
	//***************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 Extra Funtional requirement:
	// USER - Delete user *************************************************************************************************************************************
	// POSITIVE - Deleting a user
	@Test
	public void deleteUserData() {
		this.authenticate("user11");
		final User user = this.userService.findOneToEdit(this.getEntityId("user11"));
		this.userService.delete(user);

		Assert.isTrue(!this.userService.findAll().contains(user));

		this.authenticate(null);
	}

	// NEGATIVE - Deleting other user
	@Test(expected = IllegalArgumentException.class)
	public void deleteOtherUser() {
		this.authenticate("user1");
		final User user = this.userService.findOne(this.getEntityId("user2"));
		user.setName("Test name");
		this.userService.delete(user);

		Assert.isTrue(!this.userService.findAll().contains(user));

		this.authenticate(null);
	}
	// *****************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR05.3:
	// USER - Edit Rendezvous *****************************************************************************************************************************
	// This use case has 10 different test cases because it involves a listing and edition view.
	@Test
	public void driverEditRendezvous() {
		final Calendar futureCalendar = Calendar.getInstance();
		futureCalendar.set(2020, 5, 25, 11, 20);
		final Date futureDate = futureCalendar.getTime();
		final Calendar pastCalendar = Calendar.getInstance();
		pastCalendar.set(1997, 5, 25, 11, 20);
		final Date pastDate = pastCalendar.getTime();

		final String pictureUrl = "http://www.viviendomadrid.com/wp-content/uploads/2014/10/puerta-del-sol.jpg";

		final Object testingData[][] = {
			// Test 1: POSITIVE - if we change some attributes correctly,
			// the test should not fail
			{
				"user1", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, true, false, null
			},
			// Test 2: POSITIVE - if we delete some optional attributes (for
			// example, the picture), the test should not fail
			{
				"user1", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, "", true, false, null
			},
			// Test 3: POSITIVE - if we change the rendezvous to final mode,
			// the test should not fail
			{
				"user1", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, false, false, null
			},
			// Test 4: NEGATIVE - if we change some required attributes (for
			// example, the name or the description), the test should fail
			{
				"user1", "rendezvous1", "", "", futureDate, pictureUrl, true, false, IllegalArgumentException.class
			},
			// Test 5: NEGATIVE - if we change the date to a past date,
			// the test should fail
			{
				"user1", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", pastDate, pictureUrl, true, false, IllegalArgumentException.class
			},
			// Test 6: NEGATIVE - if we change the picture's URL to an
			// incorrect URL, the test should fail
			{
				"user1", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, "incorrectURL.in", true, false, IllegalArgumentException.class
			},
			// Test 7: NEGATIVE - as user who is not the creator of the
			// rendezvous the test should fail
			{
				"user2", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, true, false, IllegalArgumentException.class
			},
			// Test 8: NEGATIVE - as a no-adult user, if we change the adult
			// only mode to true, the test should fail
			{
				"user1", "rendezvous1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, true, true, IllegalArgumentException.class
			},
			// Test 9: NEGATIVE - if we try to edit a rendezvous in final
			// mode, the test should fail
			{
				"user1", "rendezvous4", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, false, false, IllegalArgumentException.class
			},
			// Test 10: NEGATIVE - if we try to edit a deleted rendezvous,
			// the test should fail
			{
				"user1", "rendezvous5", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, false, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateEditRendezvous((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Date) testingData[i][4], (String) testingData[i][5], (Boolean) testingData[i][6],
				(Boolean) testingData[i][7], (Class<?>) testingData[i][8]);
	}

	protected void templateEditRendezvous(final String username, final String rendezvousBean, final String name, final String description, final Date moment, final String picture, final Boolean draft, final Boolean adult, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			List<Rendezvous> rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());

			// First, obtain the selected rendezvous to be tested
			final int rendezvousStoredId = super.getEntityId(rendezvousBean);
			final Rendezvous rendezvousStored = this.rendezvousService.findOne(rendezvousStoredId);
			// Then, obtain it from the list of all rendezvouses (simulating
			// that the user select it in the view of list rendezvouses)
			final int rendezvousId = rendezvouses.indexOf(rendezvousStored);
			final int rendezvousInListId = rendezvouses.get(rendezvousId).getId();
			// Finally, obtain it by findOneToEdit in order to check that is not
			// in final mode or deleted
			final Rendezvous rendezvous = this.rendezvousService.findOneToEdit(rendezvousInListId);

			rendezvous.setName(name);
			rendezvous.setDescription(description);
			rendezvous.setMoment(moment);
			rendezvous.setPicture(picture);
			rendezvous.setDraft(draft);
			rendezvous.setAdultOnly(adult);

			final Rendezvous rendezvousSaved = this.rendezvousService.save(rendezvous);
			this.rendezvousService.flush();

			rendezvouses = new ArrayList<Rendezvous>(this.rendezvousService.findAll());
			Assert.isTrue(rendezvouses.contains(rendezvousSaved));
			Assert.isTrue(rendezvouses.get(rendezvouses.indexOf(rendezvousSaved)).getName().equals(name));
			Assert.isTrue(rendezvouses.size() == 7);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}

	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR05.2:
	// USER - Create Rendezvous *******************************************************************************************************
	@Test
	public void driverCreateRendezvous() {
		final Calendar futureCalendar = Calendar.getInstance();
		futureCalendar.set(2020, 5, 25, 11, 20);
		final Date futureDate = futureCalendar.getTime();
		final Calendar pastCalendar = Calendar.getInstance();
		pastCalendar.set(1997, 5, 25, 11, 20);
		final Date pastDate = pastCalendar.getTime();

		final String pictureUrl = "http://www.viviendomadrid.com/wp-content/uploads/2014/10/puerta-del-sol.jpg";

		final Object testingData[][] = {
			// POSITIVE test
			{
				"user1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, null
			},
			// NEGATIVE test: as manager the test should fail 
			{
				"manager1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, pictureUrl, IllegalArgumentException.class
			},
			// NEGATIVE test: if name and description are blank, the test
			// should fail
			{
				"user1", "", "", futureDate, pictureUrl, ConstraintViolationException.class
			},
			// NEGATIVE test: if the picture's URL is an incorrect URL, the test should fail
			{
				"user1", "Test Rendezvous", "This is a rendezvous to be tested", futureDate, "incorrectURL.in", ConstraintViolationException.class
			},
			// NEGATIVE test: if the date is a past date, the test should fail
			{
				"user1", "Test Rendezvous", "This is a rendezvous to be tested", pastDate, pictureUrl, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateCreateRendezvous((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Date) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
	}

	protected void templateCreateRendezvous(final String username, final String name, final String description, final Date moment, final String picture, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			final Rendezvous rendezvous = new Rendezvous();

			final User principal = this.userService.findByPrincipal();
			final List<User> attendants = new ArrayList<User>();
			attendants.add(principal);

			rendezvous.setName(name);
			rendezvous.setDescription(description);
			rendezvous.setMoment(moment);
			rendezvous.setPicture(picture);
			rendezvous.setDraft(true);
			rendezvous.setAdultOnly(false);
			rendezvous.setDeleted(false);

			rendezvous.setCreator(principal);
			rendezvous.setAttendants(attendants);
			rendezvous.setLinked(new ArrayList<Rendezvous>());
			rendezvous.setAnnouncements(new ArrayList<Announcement>());
			rendezvous.setRequests(new ArrayList<Request>());
			rendezvous.setQuestions(new ArrayList<Question>());

			final Rendezvous rendezvousSaved = this.rendezvousService.save(rendezvous);
			this.rendezvousService.flush();

			//Make sure the rendezvous has been created
			final Collection<Rendezvous> rendezvouses = this.rendezvousService.findAll();
			Assert.isTrue(rendezvouses.contains(rendezvousSaved));
			Assert.isTrue(rendezvouses.size() == 8);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR05.4:
	// USER - RSVP a rendezvous ************************************************************************************************************************************
	// POSITIVE
	@Test
	public void RSVPpositiveTest() {
		this.authenticate("user5");
		this.rendezvousService.RSVP(this.getEntityId("rendezvous5"));
		this.rendezvousService.flush();
		final Rendezvous rendezvous = this.rendezvousService.findOne(this.getEntityId("rendezvous5"));
		
		//Make sure the user is in the attendants list
		Assert.isTrue(rendezvous.getAttendants().contains(this.userService.findByPrincipal()));

		this.authenticate(null);
	}

	// NEGATIVE - when the rendezvous is adult-only
	@Test(expected = IllegalArgumentException.class)
	public void RSVPnegativeTest() {
		this.authenticate("user1");
		this.rendezvousService.RSVP(this.getEntityId("rendezvous2"));
		final Rendezvous rendezvous = this.rendezvousService.findOne(this.getEntityId("rendezvous2"));
		Assert.isTrue(this.userService.findByPrincipal().getRendezvouses().contains(rendezvous));
		Assert.isTrue(rendezvous.getAttendants().contains(this.userService.findByPrincipal()));

		this.authenticate(null);
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR05.5:
	// USER - List the rendezvouses that he or she’s RSVPd. ************************************************************************************************************************************
	// POSITIVE
	@Test
	public void listRSVPd() {
		this.authenticate("user1");
		List<Rendezvous> rendezvouses = (List<Rendezvous>) this.rendezvousService.findRSVPdRendezvouses();
		//Make sure every rendezvous has been RSVPd by the user
		for (Rendezvous rendezvous : rendezvouses) {
			Assert.isTrue(rendezvous.getAttendants().contains(this.userService.findByPrincipal()));
		}
		this.authenticate(null);
	}

	// NEGATIVE - An incorrect collection of RSVP rendezvouses
	@Test(expected = IllegalArgumentException.class)
	public void listRSVPdNegative() {
		this.authenticate("user1");
		List<Rendezvous> rendezvouses = (List<Rendezvous>) this.rendezvousService.findRSVPdRendezvouses();
		this.rendezvousService.cancel(rendezvouses.get(0).getId());
		for (Rendezvous rendezvous : rendezvouses) {
			Assert.isTrue(rendezvous.getAttendants().contains(this.userService.findByPrincipal()));
		}
		this.authenticate(null);
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR05.6:
	// USER - Create Comment ********************************************************************************************************************************************
	@Test
	public void driverCreateComment() {
		final Object testingData[][] = {
			// POSITIVE test
			{
				"user1", "Test Comment", new Date(), "rendezvous1", null
			},
			// NEGATIVE test: as manager should fail the test
			{
				"manager1", "Test Comment", new Date(), "rendezvous1", IllegalArgumentException.class
			},
			// NEGATIVE test: if text is blank and/or date is null, the test
			// should fail
			{
				"user1", "", null, "rendezvous1", ConstraintViolationException.class
			},
			// NEGATIVE test: if the user is not an attendant of the
			// rendezvous, the test should fail
			{
				"user3", "Test Comment", new Date(), "rendezvous1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateCreateComment((String) testingData[i][0], (String) testingData[i][1], (Date) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateCreateComment(final String username, final String text, final Date moment, final String rendezvousBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			final Comment comment = new Comment();
			final int principalId = super.getEntityId(username);
			final User principal = this.userService.findOne(principalId);
			final int rendezvousId = super.getEntityId(rendezvousBean);
			final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);

			comment.setText(text);
			comment.setMoment(moment);
			comment.setUser(principal);
			comment.setRendezvous(rendezvous);
			comment.setReplies(new ArrayList<Comment>());

			this.authenticate(username);

			final Comment commentSaved = this.commentService.save(comment);
			this.commentService.flush();

			final Collection<Comment> comments = this.commentService.findAll();
			
			//Make sure the comment has been created
			Assert.isTrue(comments.contains(commentSaved));
			Assert.isTrue(comments.size() == 12);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR016.3:
	// USER - Create ANNOUNCEMENT *******************************************************************************************************
	@Test
	public void driverCreateAnnouncement() {
		final Object testingData[][] = {
			// POSITIVE - Shouldn't fail the test
			{
				"user1", "rendezvous1", null
			},
			// NEGATIVE - Unauthenticated, should fail the test
			{
				null, "rendezvous1", AssertionError.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateCreateAnnouncement((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateCreateAnnouncement(final String username, final String rendezvousBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final Announcement announcement = new Announcement();

			int rendezvousId;
			rendezvousId = super.getEntityId(rendezvousBean);
			final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);

			int userId;
			userId = this.getEntityId(username);
			final User user = this.userService.findOne(userId);

			announcement.setTitle("Test Anouncement");
			announcement.setDescription("Description Test Announcement");
			// announcement.setMoment(Date.valueOf("18/12/2018 10:00"));
			announcement.setRendezvous(rendezvous);
			announcement.setUser(user);

			this.authenticate(username);
			Announcement result = this.announcementService.save(announcement);
			
			//Make sure the announcement has been created
			Assert.isTrue(this.announcementService.findAll().contains(result));
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR016.4:
	// USER - Link similar rendezvouses *******************************************************************************************************
	// POSITIVE - This test shouldn't fail as the rendezvouses that will be linked are available to do
	@Test
	public void linkSimilarRendezvousesPositiveTest() {
		super.authenticate("user1");
		final Rendezvous main = this.rendezvousService.findOne(super.getEntityId("rendezvous1"));
		final List<Rendezvous> availableToLink = new ArrayList<Rendezvous>(this.rendezvousService.findAllAvailableToLinkByCreator(main.getCreator().getId(), main));
		final Rendezvous selected = availableToLink.get(0);
		main.getLinked().add(selected);
		this.rendezvousService.link(main);
		selected.getLinked().add(main);
		this.rendezvousService.link(selected);
		this.rendezvousService.flush();
		
		//Make sure the rendezvouses have been linked correctly
		Assert.isTrue(main.getLinked().contains(selected) && selected.getLinked().contains(main));
		super.unauthenticate();
	}

	// NEGATIVE - This test should fail as we will try to link rendezvouses without being authenticated
	@Test(expected = IllegalArgumentException.class)
	public void linkSimilarRendezvousesNegativeTest() {
		final Rendezvous main = this.rendezvousService.findOne(super.getEntityId("rendezvous1"));
		final List<Rendezvous> availableToLink = new ArrayList<Rendezvous>(this.rendezvousService.findAllAvailableToLinkByCreator(main.getCreator().getId(), main));
		final Rendezvous selected = availableToLink.get(0);
		main.getLinked().add(selected);
		this.rendezvousService.link(main);
		selected.getLinked().add(main);
		this.rendezvousService.link(selected);
		this.rendezvousService.flush();
		Assert.isTrue(main.getLinked().contains(selected) && selected.getLinked().contains(main));
	}
	// ************************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR016.4:
	// USER - List announcements of RSVPs rendezvouses *******************************************************************************************************
	@Test
	public void driverListAnnouncementsRSVPsRendezvouses() {
		final Object testingData[][] = {
			// POSITIVE - Shouldn't fail the test
			{
				"user2", "user2", null
			},
			// NEGATIVE - Unauthenticated, should fail the test
			{
				null, "user2", IllegalArgumentException.class
			},
			// NEGATIVE - Authenticated as a different user, should fail the
			// test
			{
				"user3", "user2", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateListAnnouncementsRSVPsRendezvouses((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateListAnnouncementsRSVPsRendezvouses(final String principal, final String user, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			int userId;
			final Collection<Announcement> announcements;

			userId = super.getEntityId(user);

			this.authenticate(principal);
			announcements = this.announcementService.findAllByRsvpRendezvouses(userId);
			
			//Make sure every announcement is from a rendezvous which the principal is going to attend to
			for (Announcement announcement : announcements) {
				Assert.isTrue(announcement.getRendezvous().getAttendants().contains(this.userService.findByPrincipal()));
			}
			
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ************************************************************************************************************************************************************
	// Acme-Rendezvous 1.0 FR021.1:
	// USER - List questions of a rendezvous *******************************************************************************************************
	// POSITIVE test: The user lists the questions of a rendezvous (the fixture contains 6 questions for rendezvous 1)
	@Test
	public void listQuestions() {
		this.authenticate("user1");

		final Collection<Question> questions = this.rendezvousService.findOne(this.getEntityId("rendezvous1")).getQuestions();
		
		//The fixture contains 6 questions for rendezvous1
		Assert.isTrue(questions.size() == 6);

		this.authenticate(null);
	}

	// NEGATIVE test: The user lists an incorrect number of questions of a rendezvous (the fixture contains 6 questions for rendezvous 1)
	@Test
	public void listIncorrectQuestions() {
		this.authenticate("user1");

		final Collection<Question> questions = this.rendezvousService.findOne(this.getEntityId("rendezvous1")).getQuestions();
		final Question q = (Question) questions.toArray()[0];
		questions.remove(q);
		Assert.isTrue(!(questions.size() == 6));

		this.authenticate(null);
	}
	// *****************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR021.1:
	// USER - Add question about a rendezvous *******************************************************************************************************
	// POSITIVE test: The user adds a question to his or her rendezvous
	@Test
	public void addQuestionToRendezvous() {
		this.authenticate("user1");
		final Question question = this.questionService.create(this.getEntityId("rendezvous1"));
		question.setStatement("Is this really a test?");
		question.setAnswers(new ArrayList<Answer>());
		question.setCreator(this.userService.findByPrincipal());

		final Question result = this.questionService.save(question);
		this.questionService.flush();
		
		//Make sure the question has been created
		Assert.isTrue(this.rendezvousService.findOne(this.getEntityId("rendezvous1")).getQuestions().contains(result));

		this.authenticate(null);
	}

	// NEGATIVE test: The user adds a question to a rendezvous which belongs to other user
	@Test(expected = IllegalArgumentException.class)
	public void addQuestionToAnothersRendezvous() {
		this.authenticate("user2");
		final Question question = this.questionService.create(this.getEntityId("rendezvous1"));
		question.setStatement("Are you a registered citizen?");

		final Question result = this.questionService.save(question);
		Assert.isTrue(this.rendezvousService.findOne(this.getEntityId("rendezvous1")).getQuestions().contains(result));

		this.authenticate(null);
	}
	// *****************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR021.1:
	// USER - Edit question about rendezvous ***************************************************************************************************************
	// POSITIVE test: The user edits a question from his or her rendezvous
	@Test
	public void editQuestionFromRendezvous() {
		this.authenticate("user1");
		final Rendezvous r = this.rendezvousService.findOne(this.getEntityId("rendezvous1"));
		Question question = (Question) r.getQuestions().toArray()[0];
		question = this.questionService.findOneToEdit(question.getId());

		question.setStatement("Test question");

		final Question result = this.questionService.save(question);
		this.questionService.flush();

		//Make sure the question has been saved
		Assert.isTrue(result.getStatement().equals("Test question"));

		this.authenticate(null);
	}

	// NEGATIVE test: The user tries to edit another user's question
	@Test(expected = IllegalArgumentException.class)
	public void editQuestionFromOther() {
		this.authenticate("user2");
		final Rendezvous r = this.rendezvousService.findOne(this.getEntityId("rendezvous1"));
		Question question = (Question) r.getQuestions().toArray()[0];
		question = this.questionService.findOneToEdit(question.getId());

		question.setStatement("Test question");

		final Question result = this.questionService.save(question);
		this.questionService.flush();

		Assert.isTrue(result.getStatement().equals("Test question"));

		this.authenticate(null);
	}
	// *****************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR021.1:
	// USER - Delete question about rendezvous ***************************************************************************************************************
	// POSITIVE test: The user deletes a question from his or her rendezvous
	@Test
	public void deleteQuestionFromRendezvous() {
		this.authenticate("user1");
		final Rendezvous r = this.rendezvousService.findOne(this.getEntityId("rendezvous1"));
		final Question question = (Question) r.getQuestions().toArray()[0];

		this.questionService.delete(question);

		//Make sure the question has been deleted
		Assert.isTrue(!this.questionService.findAll().contains(question));

		this.authenticate(null);
	}

	// POSITIVE test: The user tries to delete a null question
	@Test(expected = IllegalArgumentException.class)
	public void deleteNullQuestion() {
		this.authenticate("user1");

		this.questionService.delete(null);

		this.authenticate(null);
	}
	// *****************************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR021.2:
	// USER - Answer question about rendezvous ***************************************************************************************************************
	// POSITIVE test: The user answers a question
	@Test
	public void answerQuestion() {
		this.authenticate("user4");
		final Rendezvous r = this.rendezvousService.findOne(this.getEntityId("rendezvous1"));
		final Question question = (Question) r.getQuestions().toArray()[0];

		final Answer answer = this.answerService.create();
		answer.setQuestion(question);
		answer.setResponse("Test response");

		final Answer result = this.answerService.save(answer);
		this.answerService.flush();

		//Make sure the question has been answered
		Assert.isTrue(this.userService.findByPrincipal().getAnswers().contains(result));

		this.authenticate(null);
	}

	// NEGATIVE test: The user answers a question to a rendezvous which he hasn't RSVPd
	@Test(expected = IllegalArgumentException.class)
	public void answerQuestionNotRSVPd() {
		this.authenticate("user4");
		final Rendezvous r = this.rendezvousService.findOne(this.getEntityId("rendezvous2"));
		final Question question = (Question) r.getQuestions().toArray()[0];

		final Answer answer = this.answerService.create();
		answer.setQuestion(question);
		answer.setResponse("Test response");

		final Answer result = this.answerService.save(answer);
		this.answerService.flush();

		Assert.isTrue(this.userService.findByPrincipal().getAnswers().contains(result));

		this.authenticate(null);
	}
	// *****************************************************************************************************************************************************
	// Acme-Rendezvous 2.0 FR04.3: See ManagerTest test case for funtional requirement 5.1
	
	// Acme-Rendezvous 2.0 FR04.3:
	// USER - Request a service for one of the rendezvouses that he or she’s created ***************************************************************************************************************
	// POSITIVE test
	@Test
	public void requestService() {
		this.authenticate("user1");
		final User principal = this.userService.findByPrincipal();
		final AcmeService service = (AcmeService) this.acmeServiceService.findAll().toArray()[0];
		final Rendezvous rendezvous = (Rendezvous) principal.getRendezvouses().toArray()[0];

		final Request request = this.requestService.create();
		request.setCreditCardNumber("4485 7390 4848 4694");
		request.setExpirationMonth(10);
		request.setExpirationYear(2019);
		request.setSecurityNumber(453);

		Request result = this.requestService.save(request, service, rendezvous);
		
		Assert.notNull(this.requestService.findOne(result.getId()));

		this.authenticate(null);
	}

	// NEGATIVE - when the credit card has expired
	@Test(expected = IllegalArgumentException.class)
	public void requestServiceExpiredCreditCard() {
		this.authenticate("user1");
		final User principal = this.userService.findByPrincipal();
		final AcmeService service = (AcmeService) this.acmeServiceService.findAll().toArray()[0];
		final Rendezvous rendezvous = (Rendezvous) principal.getRendezvouses().toArray()[0];

		final Request request = this.requestService.create();
		request.setCreditCardNumber("4485 7390 4848 4694");
		request.setExpirationMonth(10);
		request.setExpirationYear(2017);
		request.setSecurityNumber(453);

		this.requestService.save(request, service, rendezvous);

		this.authenticate(null);
	}
	// ************************************************************************************************************************************************************

}
