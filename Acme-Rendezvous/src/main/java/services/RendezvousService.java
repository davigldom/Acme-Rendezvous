
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.RendezvousRepository;
import domain.Administrator;
import domain.Announcement;
import domain.Answer;
import domain.Comment;
import domain.Question;
import domain.Rendezvous;
import domain.Request;
import domain.User;

@Service
@Transactional
public class RendezvousService {

	@Autowired
	private RendezvousRepository	rendezvousRepository;

	@Autowired
	private UserService				userService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private AnnouncementService		announcementService;

	@Autowired
	private CommentService			commentService;

	@Autowired
	private AnswerService			answerService;

	@Autowired
	private QuestionService			questionService;

	@Autowired
	private ActorService			actorService;


	public Rendezvous create() {
		final Rendezvous result;

		result = new Rendezvous();

		return result;
	}

	public Rendezvous findOne(final int rendezvousId) {
		Rendezvous result;
		Assert.isTrue(rendezvousId != 0);
		result = this.rendezvousRepository.findOne(rendezvousId);
		Assert.notNull(result);
		return result;
	}

	public Rendezvous save(final Rendezvous rendezvous) {
		Rendezvous result;
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(rendezvous);

		if (rendezvous.getId() != 0) {

			Assert.isTrue(rendezvous.getCreator().equals(principal));

			if (rendezvous.isAdultOnly())
				Assert.isTrue(this.actorService.isAdult());

		}
		Assert.isTrue(rendezvous.getMoment().after(new Date()));
		result = this.rendezvousRepository.save(rendezvous);
		return result;
	}

	public Rendezvous link(final Rendezvous rendezvous) {
		Rendezvous result;
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(rendezvous);
		Assert.isTrue(rendezvous.getCreator().equals(principal));
		if (rendezvous.isAdultOnly())
			Assert.isTrue(this.actorService.isAdult());

		Assert.isTrue(!rendezvous.isDeleted());

		result = this.rendezvousRepository.save(rendezvous);
		return result;
	}

	public void delete(final Rendezvous rendezvous) {
		Administrator admin;
		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		Assert.notNull(rendezvous);

		final Collection<Announcement> announcements = this.announcementService.findAllByRendezvous(rendezvous.getId());
		for (final Announcement a : announcements)
			this.announcementService.delete(a);

		final Collection<Comment> comments = this.commentService.findAllByRendezvous(rendezvous.getId());

		for (final Comment c : comments)
			this.commentService.delete(c);

		for (final Rendezvous r : rendezvous.getLinked())
			r.getLinked().remove(rendezvous);

		for (final Question q : rendezvous.getQuestions())
			this.questionService.delete(q);

		rendezvous.setCreator(null);
		rendezvous.setAttendants(null);
		rendezvous.getLinked().clear();
		this.rendezvousRepository.delete(rendezvous);
	}

	public void virtualDelete(final Rendezvous rendezvous) {
		final User principal = this.userService.findByPrincipal();
		Assert.notNull(rendezvous);
		Assert.isTrue(rendezvous.getId() != 0);
		Assert.isTrue(rendezvous.getMoment().after(new Date()));

		Assert.isTrue(rendezvous.getCreator().equals(principal));
		Assert.isTrue(rendezvous.isDraft());
		Assert.isTrue(!rendezvous.isDeleted());

		rendezvous.setDeleted(true);

		this.rendezvousRepository.save(rendezvous);
	}

	public Rendezvous findOneToEdit(final int rendezvousId) {
		Rendezvous result;
		final User principal = this.userService.findByPrincipal();

		Assert.isTrue(rendezvousId != 0);
		result = this.rendezvousRepository.findOne(rendezvousId);
		Assert.notNull(result);
		Assert.isTrue(result.getCreator().equals(principal));
		Assert.isTrue(result.isDraft());
		Assert.isTrue(!result.isDeleted());
		Assert.isTrue(result.getMoment().after(new Date()));
		return result;
	}

	public Rendezvous findOneToLink(final int rendezvousId) {
		Rendezvous result;
		final User principal = this.userService.findByPrincipal();

		Assert.isTrue(rendezvousId != 0);
		result = this.rendezvousRepository.findOne(rendezvousId);
		Assert.notNull(result);
		Assert.isTrue(result.getCreator().equals(principal));
		return result;
	}

	public void flush() {
		this.rendezvousRepository.flush();
	}

	public Collection<Rendezvous> findLinked(final int rendezvousId) {
		final Rendezvous main = this.findOne(rendezvousId);
		final Collection<Rendezvous> linked = new HashSet<Rendezvous>(main.getLinked());
		if (!this.userService.isAuthenticated() || !this.actorService.isAdult())
			for (final Rendezvous r : linked)
				if (r.isAdultOnly())
					linked.remove(r);

		return linked;
	}

	public Rendezvous unLink(final Rendezvous main, final Rendezvous aux) {
		main.getLinked().remove(aux);
		this.rendezvousRepository.save(main);
		aux.getLinked().remove(main);
		this.rendezvousRepository.save(aux);

		return aux;
	}

	public void checkAreLinked(final Rendezvous main, final Rendezvous aux) {
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(main.getId() != 0 && aux.getId() != 0);
		Assert.notNull(main);
		Assert.notNull(aux);
		Assert.isTrue(main.getCreator().equals(principal) && aux.getCreator().equals(principal));
		Assert.isTrue(main.getLinked().contains(aux) && aux.getLinked().contains(main));
	}

	public Collection<Rendezvous> findAll() {
		return this.rendezvousRepository.findAll();
	}

	public Collection<Rendezvous> findAllByCreator(final int userId) {
		return this.rendezvousRepository.findAllByCreator(userId);
	}

	public Collection<Rendezvous> findAllAvailableToLinkByCreator(final int userId, final Rendezvous rendezvous) {
		final Collection<Rendezvous> available = this.rendezvousRepository.findAllAvailableToLinkByCreator(userId, rendezvous.getId());
		Assert.notNull(available);
		available.removeAll(rendezvous.getLinked());

		return available;
	}

	public void RSVP(final int rendezvousId) {
		Assert.notNull(rendezvousId);

		final User principal = this.userService.findByPrincipal();
		Assert.notNull(principal);

		final Rendezvous rendezvous = this.findOne(rendezvousId);
		Assert.notNull(rendezvous);

		if (rendezvous.isAdultOnly() == true)
			Assert.isTrue(this.actorService.isAdult());

		final Date now = new Date();
		Assert.isTrue(rendezvous.getMoment().after(now));

		Assert.isTrue(!rendezvous.getAttendants().contains(principal));
		rendezvous.getAttendants().add(principal);

		final List<Answer> answers = new ArrayList<Answer>(this.answerService.findAllByRendezvous(rendezvousId));
		final List<Question> questions = new ArrayList<Question>(this.questionService.findAllByRendezvous(rendezvousId));
		Assert.isTrue(answers.size() == questions.size());

		this.userService.save(principal);

	}

	public void cancel(final int rendezvousId) {
		Assert.notNull(rendezvousId);

		final User principal = this.userService.findByPrincipal();
		Assert.notNull(principal);

		final Rendezvous rendezvous = this.findOne(rendezvousId);
		Assert.notNull(rendezvous);
		Assert.isTrue(rendezvous.getAttendants().contains(principal));
		rendezvous.getAttendants().remove(principal);

		this.userService.save(principal);
	}

	public Collection<Rendezvous> findRSVPdRendezvouses() {
		Collection<Rendezvous> result;
		User principal;

		principal = this.userService.findByPrincipal();
		result = this.rendezvousRepository.findRSVPdRendezvouses(principal.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findRSVPdRendezvousesNoAdult() {
		Collection<Rendezvous> result;
		User principal;

		principal = this.userService.findByPrincipal();
		result = this.rendezvousRepository.findRSVPdRendezvousesNoAdult(principal.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findRSVPdRendezvousesNoAdultById(final int actorId) {
		Collection<Rendezvous> result;

		result = this.rendezvousRepository.findRSVPdRendezvousesNoAdult(actorId);
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findCreatedRendezvouses() {
		Collection<Rendezvous> result;
		User principal;

		principal = this.userService.findByPrincipal();
		result = this.rendezvousRepository.findAllByCreator(principal.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findCreatedRendezvousesNoAdult() {
		Collection<Rendezvous> result;
		User principal;

		principal = this.userService.findByPrincipal();
		result = this.rendezvousRepository.findAllByCreatorNoAdult(principal.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findAllNoAdult() {
		Collection<Rendezvous> result;

		result = this.rendezvousRepository.findAllNoAdult();
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findNoAdultByCategory(final int categoryId) {
		Collection<Rendezvous> result;

		result = this.rendezvousRepository.findNoAdultByCategory(categoryId);
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findByCategory(final int categoryId) {
		Collection<Rendezvous> result;

		result = this.rendezvousRepository.findByCategory(categoryId);
		Assert.notNull(result);

		return result;
	}

	public Collection<Rendezvous> findRSVPdRendezvousesPerUser(final int userId) {
		Collection<Rendezvous> result;
		result = this.rendezvousRepository.findRSVPdRendezvouses(userId);
		Assert.notNull(result);

		return result;

	}

	public Double averageRendezvousesPerUser() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.averageRendezvousesPerUser();
		return result;
	}

	public Double standardDeviationRendezvousesPerUser() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.standardDeviationRendezvousesPerUser();
		return result;
	}

	public Double everCreateRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.everCreateRendezvous();
		return result;
	}

	// public Double neverCreateRendezvous() {
	// Administrator admin;
	// Collection<User> users;
	//
	// admin = this.administratorService.findByPrincipal();
	// Assert.notNull(admin);
	//
	// users = this.userService.findAll();
	//
	// Double result;
	// result = users.size() - this.rendezvousRepository.everCreateRendezvous();
	// return result;
	// }

	public Double averageUsersPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.averageUsersPerRendezvous();
		return result;
	}

	public Double standardDeviationUsersPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.standardDeviationUsersPerRendezvous();
		return result;
	}

	public Double averageRSVPdRendezvousesPerUser() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.averageRSVPdRendezvousesPerUser();
		return result;
	}

	public Double standardDeviationRSVPdRendezvousesPerUser() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.standardDeviationRSVPdRendezvousesPerUser();
		return result;
	}

	public Collection<Rendezvous> selectAbove75PercentAnnouncements() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Collection<Rendezvous> result;
		result = this.rendezvousRepository.selectAbove75PercentAnnouncements();
		return result;
	}

	public Collection<Rendezvous> selectAboveTenPercentPlusAverageAnnouncements() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Collection<Rendezvous> result;
		result = this.rendezvousRepository.selectAboveTenPercentPlusAverageAnnouncements();
		return result;
	}

	public List<Rendezvous> top10RSVPdRendezvouses() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		List<Rendezvous> result;
		result = this.rendezvousRepository.top10RSVPdRendezvouses(new PageRequest(0, 10));
		return result;
	}

	public Double averageQuestionsPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.averageQuestionsPerRendezvous();
		return result;
	}

	public Double standardDeviationQuestionsPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.standardDeviationQuestionsPerRendezvous();
		return result;
	}

	public Double averageAnswersToQuestionsPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.averageAnswersToQuestionsPerRendezvous();
		return result;
	}

	public Double standardDeviationAnswersToQuestionsPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.standardDeviationAnswersToQuestionsPerRendezvous();
		return result;
	}

	public Double averageRepliesPerComment() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.averageRepliesPerComment();
		return result;
	}

	public Double standardDeviationRepliesPerComment() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.rendezvousRepository.standardDeviationRepliesPerComment();
		return result;
	}

	// Returns rendezvous requested.
	public Rendezvous findByRequest(final int requestId) {
		Rendezvous result;
		result = this.rendezvousRepository.findByRequest(requestId);
		return result;
	}

	public Collection<Rendezvous> findWithoutService(final int serviceId) {
		Collection<Rendezvous> result;
		final int principalId = this.userService.findByPrincipal().getId();

		result = this.findAllByCreator(principalId);
		result.removeAll(this.rendezvousRepository.findWithService(serviceId, principalId));

		return result;
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Rendezvous reconstruct(final Rendezvous rendezvous, final BindingResult binding) {
		Rendezvous rendezvousStored;

		if (rendezvous.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			final Collection<User> attendants = new ArrayList<>();
			final Collection<Rendezvous> linked = new ArrayList<>();
			final Collection<Announcement> announcements = new ArrayList<>();
			final Collection<Question> questions = new ArrayList<>();
			final Collection<Request> requests = new ArrayList<>();
			attendants.add(principal);
			rendezvous.setCreator(principal);
			rendezvous.setAttendants(attendants);
			rendezvous.setLinked(linked);
			rendezvous.setAnnouncements(announcements);
			rendezvous.setQuestions(questions);
			rendezvous.setDeleted(false);
			rendezvous.setDraft(true);
			rendezvous.setAdultOnly(false);
			rendezvous.setRequests(requests);

		} else {
			rendezvousStored = this.rendezvousRepository.findOne(rendezvous.getId());

			rendezvous.setAnnouncements(rendezvousStored.getAnnouncements());
			rendezvous.setAttendants(rendezvousStored.getAttendants());
			rendezvous.setCreator(rendezvousStored.getCreator());
			rendezvous.setDeleted(rendezvousStored.isDeleted());
			rendezvous.setId(rendezvousStored.getId());
			rendezvous.setLinked(rendezvousStored.getLinked());
			rendezvous.setQuestions(rendezvousStored.getQuestions());
			rendezvous.setVersion(rendezvousStored.getVersion());
			rendezvous.setRequests(rendezvousStored.getRequests());

		}
		this.validator.validate(rendezvous, binding);
		return rendezvous;

	}

}
