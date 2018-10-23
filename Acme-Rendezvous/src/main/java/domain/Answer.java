
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Answer extends DomainEntity {

	private String	response;


	@NotBlank
	public String getResponse() {
		return this.response;
	}

	public void setResponse(final String response) {
		this.response = response;
	}


	// Relationship

	private Question	question;


	@NotNull
	@Valid
	@ManyToOne
	public Question getQuestion() {
		return this.question;
	}

	public void setQuestion(final Question question) {
		this.question = question;
	}

}
