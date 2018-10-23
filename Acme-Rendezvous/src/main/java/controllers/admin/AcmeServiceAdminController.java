
package controllers.admin;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AcmeServiceService;
import services.AdministratorService;
import controllers.AbstractController;
import domain.AcmeService;
import domain.Administrator;

@Controller
@RequestMapping("/acme-service/admin")
public class AcmeServiceAdminController extends AbstractController {

	@Autowired
	private AcmeServiceService		serviceService;

	@Autowired
	private AdministratorService	adminService;


	//Cancel service
	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public ModelAndView cancel(@RequestParam final int serviceId) {
		ModelAndView result;
		final AcmeService service = this.serviceService.findOne(serviceId);
		try {
			final Administrator principal = this.adminService.findByPrincipal();
			Assert.notNull(principal);
			this.serviceService.cancel(service);
			result = this.createEditModelAndView(service, "service.cancel.ok");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(service, "service.cancel.error");
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(final AcmeService acmeService) {
		ModelAndView result;

		result = this.createEditModelAndView(acmeService, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final AcmeService acmeService, final String message) {
		ModelAndView result = null;
		Collection<AcmeService> services;

		services = this.serviceService.findAll();

		result = new ModelAndView("acme-service/list");

		result.addObject("services", services);
		result.addObject("listManager", false);
		result.addObject("requestURI", "acme-service/list.do");
		result.addObject("message", message);

		return result;
	}

}
