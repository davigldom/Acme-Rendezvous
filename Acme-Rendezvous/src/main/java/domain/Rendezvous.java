
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = {
	@Index(columnList = "adultOnly")
})
public class Rendezvous extends DomainEntity {

	private String		name;
	private String		description;
	private Date		moment;
	private String		picture;
	private Location	coordinates;
	private boolean		draft;
	private boolean		deleted;
	private boolean		adultOnly;


	@NotBlank
	public String getName() {
		return this.name;
	}
	public void setName(final String name) {
		this.name = name;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getMoment() {
		return this.moment;
	}

	public void setMoment(final Date moment) {
		this.moment = moment;
	}

	@URL
	public String getPicture() {
		return this.picture;
	}

	public void setPicture(final String picture) {
		this.picture = picture;
	}

	public Location getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(final Location coordinates) {
		this.coordinates = coordinates;
	}

	public boolean isDraft() {
		return this.draft;
	}

	public void setDraft(final boolean draft) {
		this.draft = draft;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isAdultOnly() {
		return this.adultOnly;
	}

	public void setAdultOnly(final boolean adultOnly) {
		this.adultOnly = adultOnly;
	}


	// Relationships

	private User						creator;
	private Collection<User>			attendants;

	private Collection<Rendezvous>		linked;

	@OrderBy("moment")
	private Collection<Announcement>	announcements;

	private Collection<Request>			requests;

	private Collection<Question>		questions;


	@NotNull
	@Valid
	@ManyToOne
	public User getCreator() {
		return this.creator;
	}

	public void setCreator(final User creator) {
		this.creator = creator;
	}

	@NotNull
	@Valid
	@ManyToMany
	public Collection<User> getAttendants() {
		return this.attendants;
	}

	public void setAttendants(final Collection<User> attendants) {
		this.attendants = attendants;
	}

	@Valid
	@NotNull
	@OneToMany(mappedBy = "rendezvous")
	public Collection<Announcement> getAnnouncements() {
		return this.announcements;
	}

	public void setAnnouncements(final Collection<Announcement> announcements) {
		this.announcements = announcements;
	}

	@Valid
	@NotNull
	@ManyToMany
	public Collection<Rendezvous> getLinked() {
		return this.linked;
	}

	public void setLinked(final Collection<Rendezvous> linked) {
		this.linked = linked;
	}

	@NotNull
	@Valid
	@OneToMany(mappedBy = "rendezvous")
	public Collection<Question> getQuestions() {
		return this.questions;
	}

	public void setQuestions(final Collection<Question> questions) {
		this.questions = questions;
	}

	@NotNull
	@Valid
	@OneToMany
	public Collection<Request> getRequests() {
		return this.requests;
	}
	public void setRequests(final Collection<Request> requests) {
		this.requests = requests;
	}

}
