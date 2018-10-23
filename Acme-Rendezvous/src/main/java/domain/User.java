
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class User extends Actor {

	//Relationships

	private Collection<Rendezvous>	rendezvouses;
	private Collection<Answer>		answers;


	//	private Collection<Request>		requests;

	@Valid
	@NotNull
	@OneToMany(mappedBy = "creator")
	public Collection<Rendezvous> getRendezvouses() {
		return this.rendezvouses;
	}

	public void setRendezvouses(final Collection<Rendezvous> rendezvouses) {
		this.rendezvouses = rendezvouses;
	}

	@NotNull
	@Valid
	@OneToMany
	public Collection<Answer> getAnswers() {
		return this.answers;
	}

	public void setAnswers(final Collection<Answer> answers) {
		this.answers = answers;
	}

	//	@NotNull
	//	@Valid
	//	@OneToMany
	//	public Collection<Request> getRequests() {
	//		return requests;
	//	}
	//
	//	public void setRequests(Collection<Request> requests) {
	//		this.requests = requests;
	//	}

}
