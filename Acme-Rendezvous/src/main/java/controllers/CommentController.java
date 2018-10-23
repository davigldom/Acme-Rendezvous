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
import services.CommentService;
import services.RendezvousService;
import services.UserService;
import domain.Comment;
import domain.Rendezvous;
import domain.User;

@Controller
@RequestMapping("/comment")
public class CommentController extends AbstractController {

	// Constructors -----------------------------------------------------------

	@Autowired
	private CommentService		commentService;

	@Autowired
	private UserService			userService;

	@Autowired
	private RendezvousService	rendezvousService;

	@Autowired
	private ActorService		actorService;


	public CommentController() {
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
			final Collection<Comment> comments = this.commentService.findAllByRendezvous(rendezvousId);
			final Collection<Comment> replies = this.commentService.findAllReplies();

			//We remove all replies
			comments.removeAll(replies);

			boolean isAttendant = false;
			/*
			 * If the user is authenticated, we must check if
			 * he or she is an attendant to the rendezvous
			 */
			if (authentication.getAuthorities().toArray()[0].toString().equals("USER")) {
				final User principal = this.userService.findByPrincipal();
				if (rendezvous.getAttendants().contains(principal))
					isAttendant = true;
			}

			//Create a copy of the comments collection, but without the replies

			result = new ModelAndView("comment/list");
			result.addObject("isAttendant", isAttendant);
			result.addObject("comments", comments);
			result.addObject("rendezvousId", rendezvousId);
		}
		return result;
	}
	@RequestMapping(value = "/listReplies", method = RequestMethod.GET)
	public ModelAndView listReplies(@RequestParam final int commentId) {
		ModelAndView result;
		final Comment comment = this.commentService.findOne(commentId);
		boolean isAttendant = false;
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!authentication.getAuthorities().toArray()[0].toString().equals("ADMIN") && (comment.getRendezvous().isAdultOnly() && (!this.userService.isAuthenticated() || !this.actorService.isAdult())))
			result = new ModelAndView("welcome/index");
		else {
			/*
			 * If the user is authenticated, we must check if
			 * he or she is an attendant to the rendezvous
			 */
			if (authentication.getAuthorities().toArray()[0].toString().equals("USER")) {
				final User principal = this.userService.findByPrincipal();
				if (comment.getRendezvous().getAttendants().contains(principal))
					isAttendant = true;
			}

			result = new ModelAndView("comment/list");
			result.addObject("isAttendant", isAttendant);
			result.addObject("isRepliesList", true);
			result.addObject("comment", comment);
			result.addObject("comments", comment.getReplies());
			result.addObject("rendezvousId", comment.getRendezvous().getId());
		}
		return result;
	}
}
