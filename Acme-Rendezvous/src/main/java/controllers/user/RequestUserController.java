
package controllers.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AcmeServiceService;
import services.RendezvousService;
import services.RequestService;
import controllers.AbstractController;
import domain.AcmeService;
import domain.Rendezvous;
import domain.Request;

@Controller
@RequestMapping("/request/user")
public class RequestUserController extends AbstractController {

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private RequestService		requestService;

	@Autowired
	private AcmeServiceService	acmeServiceService;


	public RequestUserController() {
		super();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int serviceId) {
		ModelAndView result;
		Request request;
		request = this.requestService.create();
		result = this.createEditModelAndView(request, serviceId);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@RequestParam final int serviceId, @RequestParam("rendezvousId") final int rendezvousId, Request request, final BindingResult binding) {
		ModelAndView result;
		final AcmeService service = this.acmeServiceService.findOne(serviceId);

		request = this.requestService.reconstruct(request, service, binding);

		if (rendezvousId == 0) {
			result = this.createEditModelAndView(request, serviceId);
			result.addObject("errorRendezvous", "request.rendezvous.error");

		} else if (binding.hasErrors())
			result = this.createEditModelAndView(request, serviceId);

		else
			try {
				final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
				this.requestService.save(request, service, rendezvous);
				result = new ModelAndView("redirect:/rendezvous/display.do?rendezvousId=" + rendezvousId);
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(request, serviceId, "request.commit.error");
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Request request, final int serviceId) {
		ModelAndView result;

		result = this.createEditModelAndView(request, serviceId, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Request request, final int serviceId, final String message) {
		ModelAndView result = null;
		final Collection<Rendezvous> rendezvouses = this.rendezvousService.findWithoutService(serviceId);

		result = new ModelAndView("request/create");

		result.addObject("request", request);
		result.addObject("serviceId", serviceId);
		result.addObject("rendezvouses", rendezvouses);

		result.addObject("message", message);

		return result;
	}

}
