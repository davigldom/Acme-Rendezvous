
package controllers.manager;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AcmeServiceService;
import services.CategoryService;
import controllers.AbstractController;
import domain.AcmeService;
import domain.Category;

@Controller
@RequestMapping("/acme-service/manager")
public class AcmeServiceManagerController extends AbstractController {

	@Autowired
	private AcmeServiceService	acmeServiceService;

	@Autowired
	private CategoryService categoryService;



	public AcmeServiceManagerController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<AcmeService> services;

		services = this.acmeServiceService.findByPrincipal();

		result = new ModelAndView("acme-service/list");
		result.addObject("services", services);
		result.addObject("listManager", true);
		result.addObject("requestURI", "acme-service/manager/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		AcmeService acmeService;
		acmeService = this.acmeServiceService.create();
		result = this.createEditModelAndView(acmeService);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int serviceId) {
		ModelAndView result;
		AcmeService acmeService;

		acmeService = this.acmeServiceService.findOneToEdit(serviceId);
		Assert.notNull(acmeService);

		result = this.createEditModelAndView(acmeService);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(AcmeService acmeService, final BindingResult binding) {
		ModelAndView result;
		if (acmeService.getId() != 0) {
			final AcmeService storedAcmeService = this.acmeServiceService.findOne(acmeService.getId());
			Assert.isTrue(!storedAcmeService.isCancelled());
		}

		acmeService = this.acmeServiceService.reconstruct(acmeService, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(acmeService);
		else
			try {
				final AcmeService acmeServiceSaved = this.acmeServiceService.save(acmeService);
				result = new ModelAndView("redirect:/acme-service/display.do?serviceId=" + acmeServiceSaved.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(acmeService, "service.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(AcmeService acmeService, final BindingResult binding) {
		ModelAndView result;
		acmeService = this.acmeServiceService.reconstruct(acmeService, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(acmeService);
		else
			try {
				this.acmeServiceService.delete(acmeService);
				result = new ModelAndView("redirect:/acme-service/manager/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(acmeService, "service.commit.error");
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

		if (acmeService.getId() != 0)
			result = new ModelAndView("acme-service/edit");
		else if (acmeService.getId() == 0)
			result = new ModelAndView("acme-service/create");

		Collection<Category> categories = this.categoryService.findAll();

		result.addObject("acmeService", acmeService);
		result.addObject("categories", categories);
		result.addObject("message", message);

		return result;
	}

}
