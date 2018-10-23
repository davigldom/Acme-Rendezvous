
package controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.AcmeServiceService;
import services.AdministratorService;
import services.AnnouncementService;
import services.CategoryService;
import services.ManagerService;
import services.RendezvousService;
import controllers.AbstractController;
import domain.Actor;
import domain.Administrator;

@Controller
@RequestMapping("/actor")
public class ActorAdministratorController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private RendezvousService		rendezvousService;

	@Autowired
	private AnnouncementService		announcementService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private AcmeServiceService		acmeServiceService;

	@Autowired
	private CategoryService			categoryService;

	@Autowired
	private ManagerService			managerService;


	// Constructors -----------------------------------------------------------

	public ActorAdministratorController() {
		super();
	}

	//Edit Administrator
	@RequestMapping(value = "/admin/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Administrator administrator, final BindingResult binding) {
		ModelAndView result;
		administrator = this.administratorService.reconstruct(administrator, binding);
		if (binding.hasErrors())
			result = this.createEditModelAndView(administrator);
		else
			try {
				this.administratorService.save(administrator);
				result = new ModelAndView("redirect:/actor/display-principal.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(administrator, "actor.commit.error");
			}
		return result;
	}

	// Show dashboard -----------------------------------------------

	@RequestMapping("/admin/dashboard")
	public ModelAndView dashboard() {
		final ModelAndView result;

		result = new ModelAndView("admin/dashboard");

		result.addObject("averageRendezvousesPerUser", this.rendezvousService.averageRendezvousesPerUser());
		result.addObject("standardDeviationRendezvousesPerUser", this.rendezvousService.standardDeviationRendezvousesPerUser());

		result.addObject("everCreateRendezvous", this.rendezvousService.everCreateRendezvous());

		result.addObject("averageUsersPerRendezvous", this.rendezvousService.averageUsersPerRendezvous());
		result.addObject("standardDeviationUsersPerRendezvous", this.rendezvousService.standardDeviationUsersPerRendezvous());

		result.addObject("averageRSVPdRendezvousesPerUser", this.rendezvousService.averageRSVPdRendezvousesPerUser());
		result.addObject("standardDeviationRSVPdRendezvousesPerUser", this.rendezvousService.standardDeviationRSVPdRendezvousesPerUser());

		result.addObject("top10RSVPdRendezvouses", this.rendezvousService.top10RSVPdRendezvouses());

		result.addObject("averageAnnouncementsPerRendezvous", this.announcementService.averageAnnouncementsPerRendezvous());
		result.addObject("standardDeviationAnnouncementsPerRendezvous", this.announcementService.standardDeviationAnnouncementsPerRendezvous());

		result.addObject("selectAbove75PercentAnnouncements", this.rendezvousService.selectAbove75PercentAnnouncements());

		result.addObject("selectAboveTenPercentPlusAverageAnnouncements", this.rendezvousService.selectAboveTenPercentPlusAverageAnnouncements());

		result.addObject("averageQuestionsPerRendezvous", this.rendezvousService.averageQuestionsPerRendezvous());
		result.addObject("standardDeviationQuestionsPerRendezvous", this.rendezvousService.standardDeviationQuestionsPerRendezvous());

		result.addObject("averageAnswersToQuestionsPerRendezvous", this.rendezvousService.averageAnswersToQuestionsPerRendezvous());
		result.addObject("standardDeviationAnswersToQuestionsPerRendezvous", this.rendezvousService.standardDeviationAnswersToQuestionsPerRendezvous());

		result.addObject("averageRepliesPerComment", this.rendezvousService.averageRepliesPerComment());
		result.addObject("standardDeviationRepliesPerComment", this.rendezvousService.standardDeviationRepliesPerComment());

		result.addObject("managersProvidingMoreServicesThanAvg", this.managerService.getManagersProvidingMoreServicesThanAverage());
		result.addObject("managersWithMoreServicesCancelled", this.managerService.getManagersWithMoreServicesCancelled());

		//--
		result.addObject("averageNumberCategoriesPerRendezvous", this.categoryService.averageNumberCategoriesPerRendezvous());
		result.addObject("averageRatioServicesPerCategory", this.acmeServiceService.averageRatioServicesPerCategory());

		result.addObject("averageServicesRequestedPerRendezvous", this.acmeServiceService.averageServicesRequestedPerRendezvous());
		result.addObject("minServicesRequestedPerRendezvous", this.acmeServiceService.minServicesRequestedPerRendezvous());
		result.addObject("maxServicesRequestedPerRendezvous", this.acmeServiceService.maxServicesRequestedPerRendezvous());
		result.addObject("standardDeviationServicesRequestedPerRendezvous", this.acmeServiceService.standardDeviationServicesRequestedPerRendezvous());

		result.addObject("top3SellingServices", this.acmeServiceService.getTop3Selling());

		return result;

	}

	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;
		result = this.createEditModelAndView(actor, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor, final String message) {
		ModelAndView result;
		result = new ModelAndView("actor/edit");
		result.addObject(actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase(), actor);
		result.addObject("authority", actor.getUserAccount().getAuthorities().toArray()[0].toString().toLowerCase());
		result.addObject("message", message);
		result.addObject("actor", actor);
		return result;
	}
}
