/*
 * CustomerController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AnnouncementService;
import services.RendezvousService;
import controllers.AbstractController;
import domain.Announcement;
import domain.Rendezvous;

@Controller
@RequestMapping("/announcement/user")
public class AnnouncementUserController extends AbstractController {

	// Constructors -----------------------------------------------------------

	@Autowired
	private AnnouncementService	announcementService;

	@Autowired
	private RendezvousService	rendezvousService;


	public AnnouncementUserController() {
		super();
	}

	@RequestMapping(value = "/listRSVP", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int userId) {
		ModelAndView result;
		final Collection<Announcement> announcements = this.announcementService.findAllByRsvpRendezvouses(userId);

		//		boolean isAttendant = false;
		//		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//
		//		/*
		//		 * If the user is authenticated, we must check if
		//		 * he or she is an attendant to the rendezvous
		//		 */
		//		if (authentication.getAuthorities().toArray()[0].toString().equals("USER")) {
		//			final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		//			final User principal = this.userService.findByPrincipal();
		//			if (rendezvous.getAttendants().contains(principal))
		//				isAttendant = true;
		//		}

		result = new ModelAndView("announcement/list");
		result.addObject("announcements", announcements);
		result.addObject("userId", userId);
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam int rendezvousId) {
		ModelAndView result;
		Announcement announcement;
		announcement = this.announcementService.create();
		result = this.createEditModelAndView(announcement);
		result.addObject("rendezvousId", rendezvousId);

		return result;
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@RequestParam int rendezvousId, Announcement announcement, final BindingResult binding) {
		ModelAndView result;
		Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		announcement.setRendezvous(rendezvous);
		announcement = this.announcementService.reconstruct(announcement, binding);
		if (binding.hasErrors()) {
			result = this.createEditModelAndView(announcement);
			result.addObject("rendezvousId", rendezvousId);
		} else
			try {
				this.announcementService.save(announcement);
				result = new ModelAndView("redirect:/announcement/list.do?rendezvousId=" + announcement.getRendezvous().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(announcement, "announcement.commit.error");
				result.addObject("rendezvousId", rendezvousId);
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Announcement announcement) {
		ModelAndView result;

		result = this.createEditModelAndView(announcement, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Announcement announcement, final String message) {
		ModelAndView result;

		result = new ModelAndView("announcement/edit");

		result.addObject("announcement", announcement);

		result.addObject("message", message);

		return result;
	}
}
