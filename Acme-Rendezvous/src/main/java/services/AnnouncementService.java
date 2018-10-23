
package services;

import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AnnouncementRepository;
import domain.Administrator;
import domain.Announcement;
import domain.User;

@Service
@Transactional
public class AnnouncementService {

	@Autowired
	private AnnouncementRepository	announcementRepository;

	@Autowired
	private UserService				userService;

	@Autowired
	private AdministratorService	administratorService;


	public Announcement create() {
		final Announcement result;
		result = new Announcement();
		return result;
	}

	public Announcement findOne(final int announcementId) {
		Announcement result;
		Assert.isTrue(announcementId != 0);
		result = this.announcementRepository.findOne(announcementId);
		Assert.notNull(result);
		return result;
	}
	
	public Collection<Announcement> findAll() {
		Collection<Announcement> result;
		result = this.announcementRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Announcement save(final Announcement announcement) {
		Announcement result;
		Assert.notNull(announcement);
		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(announcement.getRendezvous().getCreator().equals(principal) && principal.equals(announcement.getUser()));
		announcement.setMoment(new Date(System.currentTimeMillis() - 1000));

		result = this.announcementRepository.save(announcement);

		return result;
	}

	public void delete(final Announcement announcement) {
		Assert.notNull(announcement);
		Assert.notNull(this.administratorService.findByPrincipal());
		this.announcementRepository.delete(announcement);
		
	}

	public Collection<Announcement> findAllByRendezvous(final int rendezvousId) {
		Collection<Announcement> result;
		Assert.isTrue(rendezvousId != 0);
		result = this.announcementRepository.findAllByRendezvous(rendezvousId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Announcement> findAllByRsvpRendezvouses(final int userId) {
		Collection<Announcement> result;
		Assert.isTrue(userId != 0);

		final User principal = this.userService.findByPrincipal();
		Assert.isTrue(userId == principal.getId());

		result = this.announcementRepository.findAllByRsvpRendezvouses(userId);
		Assert.notNull(result);
		return result;
	}

	public Double averageAnnouncementsPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.announcementRepository.averageAnnouncementsPerRendezvous();
		return result;
	}

	public Double standardDeviationAnnouncementsPerRendezvous() {
		Administrator admin;

		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);

		Double result;
		result = this.announcementRepository.standardDeviationAnnouncementsPerRendezvous();
		return result;
	}
	
	public void flush(){
		this.announcementRepository.flush();
	}


	//Reconstruct------------------------------------------------------------------------
	@Autowired
	private Validator	validator;


	public Announcement reconstruct(final Announcement announcement, final BindingResult binding) {
		Announcement announcementStored;

		if (announcement.getId() == 0) {
			final User principal = this.userService.findByPrincipal();
			announcement.setUser(principal);
			announcement.setMoment(new Date(System.currentTimeMillis() - 1000));
		} else {
			announcementStored = this.announcementRepository.findOne(announcement.getId());
			//result.setTitle(announcement.getTitle());
			//result.setDescription(announcement.getDescription());

			announcement.setId(announcementStored.getId());
			announcement.setMoment(announcementStored.getMoment());
			announcement.setRendezvous(announcementStored.getRendezvous());
			announcement.setUser(announcementStored.getUser());
			announcement.setVersion(announcementStored.getVersion());

		}
		this.validator.validate(announcement, binding);
		return announcement;

	}

}
