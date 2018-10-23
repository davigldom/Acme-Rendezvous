/*
 * CustomerController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AnnouncementService;
import controllers.AbstractController;
import domain.Announcement;

@Controller
@RequestMapping("/announcement/admin")
public class AnnouncementAdminController extends AbstractController {

	// Constructors -----------------------------------------------------------

	@Autowired
	private AnnouncementService	announcementService;


	public AnnouncementAdminController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int announcementId) {
		ModelAndView result;
		final Announcement announcement = this.announcementService.findOne(announcementId);
		try {
			this.announcementService.delete(announcement);
			result = new ModelAndView("redirect:/announcement/list.do?rendezvousId=" + announcement.getRendezvous().getId());
		} catch (final Throwable oops) {
			result = new ModelAndView("redirect:/announcement/list.do?rendezvousId=" + announcement.getRendezvous().getId());
			result.addObject("message", "announcement.commit.error");
		}

		return result;
	}
}
