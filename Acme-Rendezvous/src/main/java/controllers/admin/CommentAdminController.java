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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdministratorService;
import services.CommentService;
import controllers.AbstractController;
import domain.Administrator;
import domain.Comment;

@Controller
@RequestMapping("/comment/admin")
public class CommentAdminController extends AbstractController {

	// Constructors -----------------------------------------------------------

	@Autowired
	private CommentService			commentService;

	@Autowired
	private AdministratorService	administratorService;


	public CommentAdminController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int commentId) {
		ModelAndView result;
		final Comment comment = this.commentService.findOne(commentId);
		try {
			final Administrator principal = this.administratorService.findByPrincipal();
			Assert.notNull(principal);
			this.commentService.delete(comment);
			result = new ModelAndView("redirect:/comment/list.do?rendezvousId=" + comment.getRendezvous().getId());
		} catch (final Throwable oops) {
			result = new ModelAndView("redirect:/comment/list.do?rendezvousId=" + comment.getRendezvous().getId());
			result.addObject("message", "comment.commit.error");
		}

		return result;
	}
}
