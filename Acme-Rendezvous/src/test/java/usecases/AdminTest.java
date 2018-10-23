
package usecases;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import services.CategoryService;
import services.CommentService;
import services.ManagerService;
import services.RendezvousService;
import services.SystemConfigService;
import utilities.AbstractTest;
import domain.AcmeService;
import domain.Announcement;
import domain.Category;
import domain.Comment;
import domain.Rendezvous;
import domain.SystemConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class AdminTest extends AbstractTest {

	// System under test

	@Autowired
	private AcmeServiceService	acmeServiceService;

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private AnnouncementService	announcementService;

	@Autowired
	private ManagerService		managerService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private CommentService		commentService;

	@Autowired
	private SystemConfigService	systemConfigService;

	DecimalFormat				df	= new DecimalFormat("#.##");	// To get only 2 decimals


	// ------------------------------------------------------ TESTS ------------------------------------------------------------------

	// Acme-Rendezvous 1.0 FR06.1:
	// ADMIN - LIST COMMENTS ***********************************************************************************************************
	// POSITIVE: The admin accesses the comments of a rendezvous (the
	// fixture contains 6 comments for rendezvous 1)
	@Test
	public void listComments() {
		this.authenticate("admin");
		final Collection<Comment> comments = this.commentService.findAllByRendezvous(this.getEntityId("rendezvous1"));
		Assert.isTrue(comments.size() == 6);
		this.authenticate(null);
	}

	// NEGATIVE: The admin accesses the comments of a rendezvous with an
	// incorrect number of comments
	// (the fixture contains 6 comments for rendezvous 1)
	@Test
	public void listIncorrectComments() {
		this.authenticate("admin");
		final ArrayList<Comment> comments = (ArrayList<Comment>) this.commentService.findAllByRendezvous(this.getEntityId("rendezvous1"));
		comments.remove(0);
		Assert.isTrue(!((comments.size()) == 6));
		this.authenticate(null);
	}
	// *********************************************************************************************************************************

	
	
	// Acme-Rendezvous 1.0 FR06.1:
	// ADMIN - DELETE COMMENT ********************************************************************************************************
	// POSITIVE: The admin removes a comment
	@Test
	public void removeComment() {
		this.authenticate("admin");
		final Comment comment = (Comment) this.commentService.findAllByRendezvous(this.getEntityId("rendezvous1")).toArray()[0];
		this.commentService.delete(comment);
		//Make sure the comment has been deleted
		Assert.isTrue(!this.commentService.findAll().contains(comment));
		this.authenticate(null);
	}

	// NEGATIVE Test: An admin tries to remove a null comment
	@Test(expected = IllegalArgumentException.class)
	public void removeCommentAsUser() {
		this.authenticate("admin");
		this.commentService.delete(null);
		this.authenticate(null);
	}
	// *********************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR06.2:
	// ADMIN - DELETE RENDEZVOUS *******************************************************************************************************
	// POSITIVE
	@Test
	public void DeleteRendezvousPositiveTest() {
		this.authenticate("admin");
		final int rendezvousId = this.getEntityId("rendezvous1");
		final Rendezvous rendezvous1 = this.rendezvousService.findOne(rendezvousId);
		this.rendezvousService.delete(rendezvous1);
		//Make sure the rendezvous has been deleted
		Assert.isTrue(!this.rendezvousService.findAll().contains(rendezvous1));
		this.authenticate(null);
	}

	// NEGATIVE: should fail because is authenticated as a user
	@Test(expected = IllegalArgumentException.class)
	public void DeleteRendezvousNegativeTest() {
		this.authenticate("user1");
		final int rendezvousId = this.getEntityId("rendezvous1");
		final Rendezvous rendezvous1 = this.rendezvousService.findOne(rendezvousId);
		this.rendezvousService.delete(rendezvous1);
		this.authenticate(null);
	}
	// *********************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR17.1:
	// ADMIN - DELETE ANNOUNCEMENT ****************************************************************************************************
	@Test
	public void driverDeleteAnnouncement() {
		final Object testingData[][] = {
			// POSITIVE - Shouldn't fail the test
			{
				"admin", "announcement1", null
			},
			// NEGATIVE - Unauthenticated, should fail the test
			{
				null, "announcement1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteAnnouncement((String) testingData[i][0], super.getEntityId((String) testingData[i][1]), (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteAnnouncement(final String username, final int beanId, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final Announcement announcement = this.announcementService.findOne(beanId);

			this.announcementService.delete(announcement);
			this.announcementService.flush();
			//Make sure the announcement has been deleted
			Assert.isTrue(!this.announcementService.findAll().contains(announcement));
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR06.1:
	// ADMIN - CANCEL SERVICE *****************************************************************************************************************
	// POSITIVE
	@Test
	public void cancelService() {
		this.authenticate("admin");
		AcmeService service = (AcmeService) this.acmeServiceService.findAll().toArray()[0];
		service = this.acmeServiceService.findOne(service.getId());
		this.acmeServiceService.cancel(service);
		Assert.isTrue(this.acmeServiceService.findOne(service.getId()).isCancelled());
		this.authenticate(null);
	}

	// NEGATIVE - Not authenticated, should fail
	@Test(expected = IllegalArgumentException.class)
	public void cancelServiceUnauthenticated() {
		AcmeService service = (AcmeService) this.acmeServiceService.findAll().toArray()[0];
		service = this.acmeServiceService.findOne(service.getId());
		this.acmeServiceService.cancel(service);
	}
	// ****************************************************************************************************************************************

	
	// Acme-Rendezvous 2.0 FR11.1:
	// ADMIN - LIST CATEGORIES **************************************************************************************************************
	@Test
	public void driverListCategories() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"admin", null
			},
			// NEGATIVE: as a nonexistent user should fail the test
			{
				"user23", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateListCategories((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateListCategories(final String username, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);
			final Collection<Category> categories = this.categoryService.findAll();
			final Category defaultCategory = this.categoryService.findOne(super.getEntityId("category0"));
			Assert.isTrue(categories.contains(defaultCategory));
			
			//The fixture contains five categories
			Assert.isTrue(categories.size() == 5);
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR11.1:
	// ADMIN - CREATE CATEGORY *****************************************************************************************************************
	@Test
	public void driverCreateCategory() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"admin", "Test Category", "This is a category to be tested", null
			},
			// NEGATIVE: as user should fail the test
			{
				"user1", "Test Category", "This is a category to be tested", IllegalArgumentException.class
			},
			// NEGATIVE: if name and description are blank, the test
			// should fail
			{
				"admin", "", "", ConstraintViolationException.class
			},
			// NEGATIVE: if name is not unique, the test should fail
			{
				"admin", "Accessibility", "Accessibility's description", ConstraintViolationException.class
			},
			// NEGATIVE: if name is Default, the test should fail
			{
				"admin", "Default", "Default's description", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateCreateCategory((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateCreateCategory(final String username, final String name, final String description, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			final Category category = new Category();

			category.setName(name);
			category.setDescription(description);
			category.setServices(new ArrayList<AcmeService>());

			this.authenticate(username);

			final Category categorySaved = this.categoryService.save(category);
			this.categoryService.flush();

			final Collection<Category> categories = this.categoryService.findAll();
			
//			Make sure that the category has been created and that is listed
			Assert.isTrue(categories.contains(categorySaved));
			Assert.isTrue(categories.size() == 6);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR11.1:
	// ADMIN - UPDATE CATEGORY ****************************************************************************************************************
	@Test
	public void driverUpdateCategory() {
		final Object testingData[][] = {
			// POSITIVE
			{
				"admin", "category1", "Test Category", "This is a category to be tested", null
			},
			// NEGATIVE: as user should fail the test
			{
				"user1", "category1", "Test Category", "This is a category to be tested", IllegalArgumentException.class
			},
			// NEGATIVE: if name and description are blank, the test
			// should fail
			{
				"admin", "category1", "", "", ConstraintViolationException.class
			},
			// NEGATIVE: if name is not unique, the test should fail
			{
				"admin", "category1", "Disabled people", "Disabled people's description", ConstraintViolationException.class
			},
			// NEGATIVE: if name is Default, the test should fail
			{
				"admin", "category1", "Default", "Default's description", ConstraintViolationException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateUpdateCategory((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateUpdateCategory(final String username, final String categoryBean, final String name, final String description, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			List<Category> categories = new ArrayList<Category>(this.categoryService.findAll());

			// First, obtain the selected category to be tested
			final int categoryStoredId = super.getEntityId(categoryBean);
			final Category categoryStored = this.categoryService.findOne(categoryStoredId);
			// Then, obtain it from the list of all categories (simulating
			// that the user select it in the view of list categories)
			final int categoryId = categories.indexOf(categoryStored);
			final int categoryInListId = categories.get(categoryId).getId();
			// Finally, obtain it by findOneToEdit in order to check the constraints
			final Category category = this.categoryService.findOneToEdit(categoryInListId);

			category.setName(name);
			category.setDescription(description);

			final Category categorySaved = this.categoryService.save(category);
			this.categoryService.flush();

			categories = new ArrayList<Category>(this.categoryService.findAll());
			Assert.isTrue(categories.contains(categorySaved));
			Assert.isTrue(categories.get(categories.indexOf(categorySaved)).getName() == name);
			Assert.isTrue(categories.size() == 5);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR11.1:
	// ADMIN - DELETE CATEGORY ****************************************************************************************************************
	@Test
	public void driverDeleteCategory() {
		final Object testingData[][] = {
			// Positive test
			{
				"admin", "category4", null
			},
			// Negative test: as user should fail the test
			{
				"user1", "category4", IllegalArgumentException.class
			},
			// Negative test: the default category cannot be deleted so the
			// test should fail
			{
				"admin", "category0", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteCategory((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteCategory(final String username, final String categoryBean, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			this.authenticate(username);

			List<Category> categories = new ArrayList<Category>(this.categoryService.findAll());

			// First, obtain the selected category to be tested
			final int categoryStoredId = super.getEntityId(categoryBean);
			final Category categoryStored = this.categoryService.findOne(categoryStoredId);
			// Then, obtain it from the list of all categories (simulating
			// that the user select it in the view of list categories)
			final int categoryId = categories.indexOf(categoryStored);
			final Category category = categories.get(categoryId);

			this.categoryService.delete(category);
			this.categoryService.flush();

			categories = new ArrayList<Category>(this.categoryService.findAll());
			
			//Make sure it has been deleted
			Assert.isTrue(!categories.contains(category));
			Assert.isTrue(categories.size() == 4);

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 2.0 FR11.1:
	// ADMIN - CHANGE CATEGORY'S PARENT (re-organize it) *******************************************************************************************************
	@Test
	public void driverChangeParentCategory() {
		final Object testingData[][] = {
			// POSITIVE: as administrator shouldn't fail the test changing the parent to Default(category0)
			{
				"admin", "category2", "category0", null
			},
			// NEGATIVE: as user should fail the test
			{
				"user1", "category2", "category0", IllegalArgumentException.class
			},
			//NEGATIVE: the default's parent cannot be changed
			{
				"admin", "category0", "category1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.templateChangeParentCategory((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateChangeParentCategory(final String username, final String categoryBean, final String categoryBeanParent, final Class<?> expected) {
		Class<?> caught;
		caught = null;
		try {
			int categoryId;
			int categoryParentId;
			categoryId = super.getEntityId(categoryBean);
			categoryParentId = super.getEntityId(categoryBeanParent);
			final Category category = this.categoryService.findOne(categoryId);
			final Category parent = this.categoryService.findOne(categoryParentId);

			this.authenticate(username);
			category.setParent(parent);
			this.categoryService.save(category);
			this.categoryService.flush();
			
			Assert.isTrue(this.categoryService.findOne(categoryId).getParent().equals(parent));
			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		this.checkExceptions(expected, caught);
	}
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 2.0 Extra functional requirement:
	// ADMIN - EDIT SYSTEM CONFIG (BANNER, WELCOME MESSAGE...) ********************************************************************************
	@Test
	public void driverSysConfig() {
		final Object testingData[][] = {
			//POSITIVE: This test shouldn't fail.
			{
				"systemConfig", "admin", "http://www.uwyo.edu/reslife-dining/_files/re-design-images/dining-logos/rendezvouslogo_2016.png", "Test business Name 123", "Test welcome msg EN", "Test mensaje bienvenida ES", null
			},
			// NEGATIVE - This test should fail as the configuration isn't being saved by an authenticated admin.
			{
				"systemConfig", null, "http://www.uwyo.edu/reslife-dining/_files/re-design-images/dining-logos/rendezvouslogo_2016.png", "Test business Name 456", "Test welcome msg2 EN", "Test mensaje bienvenida2 ES", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateSysConfig((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateSysConfig(final String beanName, final String username, final String banner, final String businessName, final String wMsg, final String wMsgEs, final Class<?> expected) {
		Class<?> caught;
		final SystemConfig sysConf = this.systemConfigService.findConfig();
		int sysConfigId;

		caught = null;
		try {
			sysConfigId = super.getEntityId(beanName);
			Assert.isTrue(sysConfigId == sysConf.getId());

			this.authenticate(username);

			sysConf.setBanner(banner);
			sysConf.setBusinessName(businessName);
			sysConf.setWelcomeMessage(wMsg);
			sysConf.setWelcomeMessageEs(wMsgEs);

			this.systemConfigService.save(sysConf);
			
			Assert.isTrue(this.systemConfigService.findConfig().getBanner().equals(banner));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.checkExceptions(expected, caught);
	}
	
	// ****************************************************************************************************************************************

	// Acme-Rendezvous 1.0 FR06.3:
	// Acme-Rendezvous 1.0 FR17.2:
	// Acme-Rendezvous 1.0 FR22.3:
	// Acme-Rendezvous 2.0 FR06.2:
	// Acme-Rendezvous 2.0 FR011.1:

	/*
	 * Display a dashboard with the following information: -The average and the
	 * standard deviation of rendezvouses created per user -The ratio of users
	 * who have ever created a rendezvous versus the users who have never
	 * created any rendezvouses -The average and the standard deviation of users
	 * per rendezvous -The average and the standard deviation of rendezvouses
	 * that are RSVPd per user -The top-10 rendezvouses in terms of users who
	 * have RSVPd them -The average and the standard deviation of announcements
	 * per rendezvous -The rendezvouses that whose number of announcements is
	 * above 75% the average number of announcements per rendezvous -The
	 * rendezvouses that are linked to a number of rendezvouses that is greater
	 * than the average plus 10% -The average and the standard deviation of the
	 * number of questions per rendezvous -The average and the standard
	 * deviation of the number of answers to the questions per rendezvous -The
	 * average and the standard deviation of replies per comment -The
	 * best-selling services/The top-selling services -The managers who provide
	 * more services than the average -The managers who have got more services
	 * cancelled -The average number of categories per rendezvous -The average
	 * ratio of services in each category -The average, the minimum, the
	 * maximum, and the standard deviation of services requested per rendezvous
	 * This test shouldn't fail as an admin is authenticated POSITIVE
	 */
	@Test
	public void displayDashboardPositiveTest() {
		super.authenticate("admin");

		Assert.isTrue(0.64 == Double.valueOf(this.df.format(this.rendezvousService.averageRendezvousesPerUser())));
		Assert.isTrue(1.15 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRendezvousesPerUser())));
		Assert.isTrue(0.57 == Double.valueOf(this.df.format(this.rendezvousService.everCreateRendezvous())));
		Assert.isTrue(2.14 == Double.valueOf(this.df.format(this.rendezvousService.averageUsersPerRendezvous())));
		Assert.isTrue(1.96 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationUsersPerRendezvous())));
		Assert.isTrue(0.18 == Double.valueOf(this.df.format(this.rendezvousService.averageRSVPdRendezvousesPerUser())));
		Assert.isTrue(0.64 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRSVPdRendezvousesPerUser())));
		Assert.isTrue(7 == this.rendezvousService.top10RSVPdRendezvouses().size()); // We only have 6
		Assert.isTrue(0.71 == Double.valueOf(this.df.format(this.announcementService.averageAnnouncementsPerRendezvous())));
		Assert.isTrue(1.16 == Double.valueOf(this.df.format(this.announcementService.standardDeviationAnnouncementsPerRendezvous())));
		Assert.isTrue(2 == this.rendezvousService.selectAbove75PercentAnnouncements().size());
		Assert.isTrue(2 == this.rendezvousService.selectAboveTenPercentPlusAverageAnnouncements().size());
		Assert.isTrue(1.71 == Double.valueOf(this.df.format(this.rendezvousService.averageQuestionsPerRendezvous())));
		Assert.isTrue(2.05 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationQuestionsPerRendezvous())));
		Assert.isTrue(4.29 == Double.valueOf(this.df.format(this.rendezvousService.averageAnswersToQuestionsPerRendezvous())));
		Assert.isTrue(1.12 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationAnswersToQuestionsPerRendezvous())));
		Assert.isTrue(0.36 == Double.valueOf(this.df.format(this.rendezvousService.averageRepliesPerComment())));
		Assert.isTrue(0.88 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRepliesPerComment())));
		Assert.isTrue(3 == this.acmeServiceService.getTop3Selling().getSize());
		Assert.isTrue(1 == this.managerService.getManagersProvidingMoreServicesThanAverage().size());
		Assert.isTrue(3 == this.managerService.getManagersWithMoreServicesCancelled().getSize());
		Assert.isTrue(0.43 == Double.valueOf(this.df.format(this.categoryService.averageNumberCategoriesPerRendezvous())));
		Assert.isTrue(0.8 == Double.valueOf(this.df.format(this.acmeServiceService.averageRatioServicesPerCategory())));
		Assert.isTrue(0.43 == Double.valueOf(this.df.format(this.acmeServiceService.averageServicesRequestedPerRendezvous())));
		Assert.isTrue(0 == this.acmeServiceService.minServicesRequestedPerRendezvous());
		Assert.isTrue(2 == this.acmeServiceService.maxServicesRequestedPerRendezvous());
		Assert.isTrue(0.85 == Double.valueOf(this.df.format(this.acmeServiceService.standardDeviationServicesRequestedPerRendezvous())));

		super.unauthenticate();
	}

	/*
	 * Display a dashboard with the following information: -The average and the
	 * standard deviation of rendezvouses created per user -The ratio of users
	 * who have ever created a rendezvous versus the users who have never
	 * created any rendezvouses -The average and the standard deviation of users
	 * per rendezvous -The average and the standard deviation of rendezvouses
	 * that are RSVPd per user -The top-10 rendezvouses in terms of users who
	 * have RSVPd them -The average and the standard deviation of announcements
	 * per rendezvous -The rendezvouses that whose number of announcements is
	 * above 75% the average number of announcements per rendezvous -The
	 * rendezvouses that are linked to a number of rendezvouses that is greater
	 * than the average plus 10% -The average and the standard deviation of the
	 * number of questions per rendezvous -The average and the standard
	 * deviation of the number of answers to the questions per rendezvous -The
	 * average and the standard deviation of replies per comment -The
	 * best-selling services/The top-selling services -The managers who provide
	 * more services than the average -The managers who have got more services
	 * cancelled -The average number of categories per rendezvous -The average
	 * ratio of services in each category -The average, the minimum, the
	 * maximum, and the standard deviation of services requested per rendezvous
	 * This test should fail as an admin is not authenticated NEGATIVE
	 */
	@Test(expected = IllegalArgumentException.class)
	public void displayDashboardNegativeTest() {
		Assert.isTrue(0.6 == Double.valueOf(this.df.format(this.rendezvousService.averageRendezvousesPerUser())));
		Assert.isTrue(0.92 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRendezvousesPerUser())));
		Assert.isTrue(0.67 == Double.valueOf(this.df.format(this.rendezvousService.everCreateRendezvous())));
		Assert.isTrue(2.33 == Double.valueOf(this.df.format(this.rendezvousService.averageUsersPerRendezvous())));
		Assert.isTrue(1.89 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationUsersPerRendezvous())));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.rendezvousService.averageRSVPdRendezvousesPerUser())));
		Assert.isTrue(0.66 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRSVPdRendezvousesPerUser())));
		Assert.isTrue(6 == this.rendezvousService.top10RSVPdRendezvouses().size());
		Assert.isTrue(0.83 == Double.valueOf(this.df.format(this.announcementService.averageAnnouncementsPerRendezvous())));
		Assert.isTrue(1.21 == Double.valueOf(this.df.format(this.announcementService.standardDeviationAnnouncementsPerRendezvous())));
		Assert.isTrue(2 == this.rendezvousService.selectAbove75PercentAnnouncements().size());
		Assert.isTrue(2 == this.rendezvousService.selectAboveTenPercentPlusAverageAnnouncements().size());
		Assert.isTrue(2.0 == Double.valueOf(this.df.format(this.rendezvousService.averageQuestionsPerRendezvous())));
		Assert.isTrue(2.08 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationQuestionsPerRendezvous())));
		Assert.isTrue(5.0 == Double.valueOf(this.df.format(this.rendezvousService.averageAnswersToQuestionsPerRendezvous())));
		Assert.isTrue(1.12 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationAnswersToQuestionsPerRendezvous())));
		Assert.isTrue(0.36 == Double.valueOf(this.df.format(this.rendezvousService.averageRepliesPerComment())));
		Assert.isTrue(0.88 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRepliesPerComment())));
		Assert.isTrue(3 == this.acmeServiceService.getTop3Selling().getSize());
		Assert.isTrue(1 == this.managerService.getManagersProvidingMoreServicesThanAverage().size());
		Assert.isTrue(3 == this.managerService.getManagersWithMoreServicesCancelled().getSize());
		Assert.isTrue(0.5 == Double.valueOf(this.df.format(this.categoryService.averageNumberCategoriesPerRendezvous())));
		Assert.isTrue(0.8 == Double.valueOf(this.df.format(this.acmeServiceService.averageRatioServicesPerCategory())));
		Assert.isTrue(0.5 == Double.valueOf(this.df.format(this.acmeServiceService.averageServicesRequestedPerRendezvous())));
		Assert.isTrue(0 == this.acmeServiceService.minServicesRequestedPerRendezvous());
		Assert.isTrue(2 == this.acmeServiceService.maxServicesRequestedPerRendezvous());
		Assert.isTrue(0.91 == Double.valueOf(this.df.format(this.acmeServiceService.standardDeviationServicesRequestedPerRendezvous())));
	}

	/*
	 * Display a dashboard with the following information: -The average and the
	 * standard deviation of rendezvouses created per user -The ratio of users
	 * who have ever created a rendezvous versus the users who have never
	 * created any rendezvouses -The average and the standard deviation of users
	 * per rendezvous -The average and the standard deviation of rendezvouses
	 * that are RSVPd per user -The top-10 rendezvouses in terms of users who
	 * have RSVPd them -The average and the standard deviation of announcements
	 * per rendezvous -The rendezvouses that whose number of announcements is
	 * above 75% the average number of announcements per rendezvous -The
	 * rendezvouses that are linked to a number of rendezvouses that is greater
	 * than the average plus 10% -The average and the standard deviation of the
	 * number of questions per rendezvous -The average and the standard
	 * deviation of the number of answers to the questions per rendezvous -The
	 * average and the standard deviation of replies per comment -The
	 * best-selling services/The top-selling services -The managers who provide
	 * more services than the average -The managers who have got more services
	 * cancelled -The average number of categories per rendezvous -The average
	 * ratio of services in each category -The average, the minimum, the
	 * maximum, and the standard deviation of services requested per rendezvous
	 * This test should fail as some results are incorrect NEGATIVE
	 */
	@Test(expected = IllegalArgumentException.class)
	public void displayDashboardIncorrectResultsNegativeTest() {
		super.authenticate("admin");

		Assert.isTrue(0.5 == Double.valueOf(this.df.format(this.rendezvousService.averageRendezvousesPerUser())));
		Assert.isTrue(0.96 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRendezvousesPerUser())));
		Assert.isTrue(0.64 == Double.valueOf(this.df.format(this.rendezvousService.everCreateRendezvous())));
		Assert.isTrue(2.33 == Double.valueOf(this.df.format(this.rendezvousService.averageUsersPerRendezvous())));
		Assert.isTrue(1.89 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationUsersPerRendezvous())));
		Assert.isTrue(0.2 == Double.valueOf(this.df.format(this.rendezvousService.averageRSVPdRendezvousesPerUser())));
		Assert.isTrue(0.66 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRSVPdRendezvousesPerUser())));
		Assert.isTrue(7 == this.rendezvousService.top10RSVPdRendezvouses().size());
		Assert.isTrue(0.83 == Double.valueOf(this.df.format(this.announcementService.averageAnnouncementsPerRendezvous())));
		Assert.isTrue(1.21 == Double.valueOf(this.df.format(this.announcementService.standardDeviationAnnouncementsPerRendezvous())));
		Assert.isTrue(2 == this.rendezvousService.selectAbove75PercentAnnouncements().size());
		Assert.isTrue(2 == this.rendezvousService.selectAboveTenPercentPlusAverageAnnouncements().size());
		Assert.isTrue(3.3 == Double.valueOf(this.df.format(this.rendezvousService.averageQuestionsPerRendezvous())));
		Assert.isTrue(2.08 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationQuestionsPerRendezvous())));
		Assert.isTrue(5.0 == Double.valueOf(this.df.format(this.rendezvousService.averageAnswersToQuestionsPerRendezvous())));
		Assert.isTrue(1.12 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationAnswersToQuestionsPerRendezvous())));
		Assert.isTrue(1.36 == Double.valueOf(this.df.format(this.rendezvousService.averageRepliesPerComment())));
		Assert.isTrue(0.88 == Double.valueOf(this.df.format(this.rendezvousService.standardDeviationRepliesPerComment())));
		Assert.isTrue(3 == this.acmeServiceService.getTop3Selling().getSize());
		Assert.isTrue(4 == this.managerService.getManagersProvidingMoreServicesThanAverage().size());
		Assert.isTrue(3 == this.managerService.getManagersWithMoreServicesCancelled().getSize());
		Assert.isTrue(0.5 == Double.valueOf(this.df.format(this.categoryService.averageNumberCategoriesPerRendezvous())));
		Assert.isTrue(1.0 == Double.valueOf(this.df.format(this.acmeServiceService.averageRatioServicesPerCategory())));
		Assert.isTrue(2.5 == Double.valueOf(this.df.format(this.acmeServiceService.averageServicesRequestedPerRendezvous())));
		Assert.isTrue(0 == this.acmeServiceService.minServicesRequestedPerRendezvous());
		Assert.isTrue(2 == this.acmeServiceService.maxServicesRequestedPerRendezvous());
		Assert.isTrue(0.91 == Double.valueOf(this.df.format(this.acmeServiceService.standardDeviationServicesRequestedPerRendezvous())));

		super.unauthenticate();
	}

}
