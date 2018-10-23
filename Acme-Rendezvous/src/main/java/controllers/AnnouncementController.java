/*
 * CustomerController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.AnnouncementService;
import services.RendezvousService;
import services.UserService;
import domain.Announcement;
import domain.Rendezvous;

@Controller
@RequestMapping("/announcement")
public class AnnouncementController extends AbstractController {

	// Constructors -----------------------------------------------------------

	@Autowired
	private AnnouncementService	announcementService;

	@Autowired
	private UserService			userService;

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private ActorService		actorService;


	public AnnouncementController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int rendezvousId) {
		ModelAndView result;
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!authentication.getAuthorities().toArray()[0].toString().equals("ADMIN") && (rendezvous.isAdultOnly() && (!this.userService.isAuthenticated() || !this.actorService.isAdult())))
			result = new ModelAndView("welcome/index");

		else {
			final Collection<Announcement> announcements = this.announcementService.findAllByRendezvous(rendezvousId);

			final String uri = "announcement/list.do";

			result = new ModelAndView("announcement/list");
			result.addObject("requestURI", uri);
			result.addObject("announcements", announcements);
			result.addObject("rendezvousId", rendezvousId);
		}
		return result;
	}
}
