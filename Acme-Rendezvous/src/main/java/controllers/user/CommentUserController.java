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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CommentService;
import services.RendezvousService;
import controllers.AbstractController;
import domain.Comment;
import domain.Rendezvous;

@Controller
@RequestMapping("/comment/user")
public class CommentUserController extends AbstractController {

	// Constructors -----------------------------------------------------------

	@Autowired
	private CommentService		commentService;

	@Autowired
	private RendezvousService	rendezvousService;


	public CommentUserController() {
		super();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int rendezvousId) {
		ModelAndView result;
		Comment comment;
		comment = this.commentService.create();
		result = this.createEditModelAndView(comment);
		result.addObject("rendezvousId", rendezvousId);
		return result;
	}

	@RequestMapping(value = "/createReply", method = RequestMethod.GET)
	public ModelAndView createReply(@RequestParam final int commentId) {
		ModelAndView result;
		Comment reply;
		final Comment comment = this.commentService.findOne(commentId);
		reply = this.commentService.create();
		result = this.createEditReplyModelAndView(reply, null);
		result.addObject("comment", comment);
		result.addObject("rendezvousId", comment.getRendezvous().getId());

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@RequestParam final int rendezvousId, Comment comment, final BindingResult binding) {
		ModelAndView result;
		comment.setRendezvous(this.rendezvousService.findOne(rendezvousId));
		comment = this.commentService.reconstruct(comment, binding);
		if (binding.hasErrors()) {
			result = this.createEditModelAndView(comment);
			result.addObject("rendezvousId", rendezvousId);
		} else
			try {
				this.commentService.save(comment);
				result = new ModelAndView("redirect:/comment/list.do?rendezvousId=" + comment.getRendezvous().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(comment, "comment.commit.error");
				result.addObject("rendezvousId", rendezvousId);
			}
		return result;
	}

	@RequestMapping(value = "/reply", method = RequestMethod.POST, params = "reply")
	public ModelAndView reply(@RequestParam final int rendezvousId, @ModelAttribute("reply") Comment reply, final BindingResult binding, @RequestParam final int commentId) {
		ModelAndView result;
		final Rendezvous rendezvous = this.rendezvousService.findOne(rendezvousId);
		reply.setRendezvous(rendezvous);
		reply = this.commentService.reconstruct(reply, binding);
		final Comment replied = this.commentService.findOne(commentId);

		if (binding.hasErrors()) {
			result = this.createEditReplyModelAndView(reply, null);
			result.addObject("rendezvousId", rendezvousId);
			result.addObject("comment", replied);
		} else
			try {
				Assert.isTrue(rendezvous.getAttendants().contains(reply.getUser()));
				if(reply.getId()!=0) Assert.isTrue(reply.getId()!=commentId);
				Assert.isTrue(reply.getRendezvous().equals(replied.getRendezvous()));
				final Comment savedReply = this.commentService.save(reply);
				replied.getReplies().add(savedReply);
				this.commentService.saveReply(replied);
				result = new ModelAndView("redirect:/comment/list.do?rendezvousId=" + reply.getRendezvous().getId());
			} catch (final Throwable oops) {
				result = this.createEditReplyModelAndView(reply, "comment.commit.error");
				result.addObject("rendezvousId", rendezvousId);
				result.addObject("comment", replied);
			}
		return result;
	}

	protected ModelAndView createEditModelAndView(final Comment comment) {
		ModelAndView result;

		result = this.createEditModelAndView(comment, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Comment comment, final String message) {
		ModelAndView result;

		result = new ModelAndView("comment/edit");

		result.addObject("comment", comment);

		result.addObject("message", message);

		return result;
	}

	protected ModelAndView createEditReplyModelAndView(final Comment reply, final String message) {
		ModelAndView result;

		result = new ModelAndView("comment/reply");

		result.addObject("reply", reply);

		result.addObject("message", message);

		return result;
	}
}
