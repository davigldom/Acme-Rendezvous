
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AcmeServiceService;
import services.ManagerService;
import domain.AcmeService;
import domain.Manager;

@Controller
@RequestMapping("/acme-service")
public class AcmeServiceController extends AbstractController {

	@Autowired
	private AcmeServiceService	acmeServiceService;

	@Autowired
	private ManagerService		managerService;


	public AcmeServiceController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<AcmeService> services;

		services = this.acmeServiceService.findAll();

		result = new ModelAndView("acme-service/list");
		result.addObject("services", services);
		result.addObject("listManager", false);
		result.addObject("requestURI", "acme-service/list.do");

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int serviceId) {
		ModelAndView result;
		AcmeService acmeService;
		Manager creator;

		acmeService = this.acmeServiceService.findOne(serviceId);

		Assert.notNull(acmeService);
		result = new ModelAndView("acme-service/display");

		result.addObject("acmeService", acmeService);
		result.addObject("serviceId", serviceId);

		creator = this.managerService.findCreator(serviceId);

		result.addObject("creator", creator);

		return result;
	}

}
