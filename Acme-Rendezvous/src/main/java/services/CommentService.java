
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CommentRepository;
import domain.Comment;
import domain.User;

@Service
@Transactional
public class CommentService {

	@Autowired
	private CommentRepository	commentRepository;

	@Autowired
	private UserService			userService;


	public Comment create() {
		final Comment result;
		result = new Comment();
		return result;
	}

	public Comment findOne(final int commentId) {
		Comment result;
		Assert.isTrue(commentId != 0);
		result = this.commentRepository.findOne(commentId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Comment> findAll() {
		Collection<Comment> result;
		result = this.commentRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Comment save(final Comment comment) {
		Comment result;
		Assert.notNull(comment);
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(comment.getRendezvous().getAttendants().contains(principal) && principal.equals(comment.getUser()));
		comment.setMoment(new Date(System.currentTimeMillis() + 1000));

		result = this.commentRepository.save(comment);

		return result;
	}

	public Comment saveReply(final Comment replied) {
		Comment result;
		Assert.notNull(replied);
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(replied.getRendezvous().getAttendants().contains(principal));

		result = this.commentRepository.save(replied);

		return result;
	}
	public void delete(final Comment comment) {
		Assert.notNull(comment);

		final Comment root = this.findRootComment(comment.getId());
		if (root != null)
			root.getReplies().remove(comment);

		this.commentRepository.delete(comment);

	}

	public Collection<Comment> findAllByRendezvous(final int rendezvousId) {
		Collection<Comment> result;
		Assert.isTrue(rendezvousId != 0);
		result = this.commentRepository.findAllByRendezvous(rendezvousId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Comment> findAllReplies() {
		Collection<Comment> result;
		result = this.commentRepository.findAllReplies();
		Assert.notNull(result);
		return result;
	}

	public Comment findRootComment(final int replyId) {
		Comment result;
		Assert.notNull(replyId);
		result = this.commentRepository.findRootComment(replyId);
		return result;
	}

	public void flush() {
		this.commentRepository.flush();
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public Comment reconstruct(final Comment comment, final BindingResult binding) {
		Comment commentStored;

		if (comment.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			comment.setUser(principal);
			comment.setMoment(new Date(System.currentTimeMillis() + 1000));
			comment.setReplies(new ArrayList<Comment>());
		} else {
			commentStored = this.commentRepository.findOne(comment.getId());
			//			result.setPicture(comment.getPicture());
			//			result.setText(comment.getText());
			comment.setId(commentStored.getId());
			comment.setMoment(commentStored.getMoment());
			comment.setRendezvous(commentStored.getRendezvous());
			comment.setReplies(commentStored.getReplies());
			comment.setUser(commentStored.getUser());
			comment.setVersion(commentStored.getVersion());

		}
		this.validator.validate(comment, binding);
		return comment;

	}

}
