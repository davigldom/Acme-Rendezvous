
package controllers.admin;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdministratorService;
import services.RendezvousService;
import controllers.AbstractController;
import domain.Rendezvous;

@Controller
@RequestMapping("/rendezvous/admin")
public class RendezvousAdminController extends AbstractController {

	@Autowired
	private RendezvousService		rendezvousService;

	@Autowired
	private AdministratorService	adminService;


	public RendezvousAdminController() {
		super();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam int rendezvousId) {
		ModelAndView result;

		Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		try {
			Assert.notNull(this.adminService.findByPrincipal());
			this.rendezvousService.delete(rendezvous);
			result = new ModelAndView("redirect:/");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(rendezvous, "rendezvous.commit.error");
		}

		return result;
	}
	protected ModelAndView createEditModelAndView(final Rendezvous r) {
		ModelAndView result;

		result = this.createEditModelAndView(r, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Rendezvous rendezvous, final String message) {
		ModelAndView result;

		result = new ModelAndView("rendezvous/display");

		result.addObject("rendezvous", rendezvous);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		final String momentFormated = formatter.format(rendezvous.getMoment());

		result.addObject("momentFormated", momentFormated);

		result.addObject("message", message);

		return result;
	}

}
