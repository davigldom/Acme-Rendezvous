
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Entity
@Access(AccessType.PROPERTY)
public class Request extends DomainEntity {

	private String	comments;
	private String	creditCardNumber;
	private int		expirationMonth;
	private int		expirationYear;
	private int		securityNumber;


	public String getComments() {
		return this.comments;
	}
	public void setComments(final String comments) {
		this.comments = comments;
	}

	@CreditCardNumber
	@NotBlank
	@Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$")
	public String getCreditCardNumber() {
		return this.creditCardNumber;
	}
	public void setCreditCardNumber(final String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	@Range(min = 1, max = 12)
	public int getExpirationMonth() {
		return this.expirationMonth;
	}

	public void setExpirationMonth(final Integer expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	@Range(min = 2000, max = 2100)
	public int getExpirationYear() {
		return this.expirationYear;
	}

	public void setExpirationYear(final Integer expirationYear) {
		this.expirationYear = expirationYear;
	}

	@Range(min = 1, max = 999)
	public int getSecurityNumber() {
		return this.securityNumber;
	}
	public void setSecurityNumber(final int securityNumber) {
		this.securityNumber = securityNumber;
	}


	private AcmeService	service;


	@NotNull
	@Valid
	@ManyToOne
	public AcmeService getService() {
		return this.service;
	}

	public void setService(final AcmeService service) {
		this.service = service;
	}

}
