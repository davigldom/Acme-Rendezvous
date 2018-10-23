
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Manager extends Actor {

	// Attributes

	private String	vat;


	@NotBlank
	@Pattern(regexp = "^[a-zA-Z0-9-]*$")
	public String getVat() {
		return this.vat;
	}

	public void setVat(final String vat) {
		this.vat = vat;
	}


	// Relationships

	private Collection<AcmeService>	services;


	@Valid
	@NotNull
	@OneToMany
	public Collection<AcmeService> getServices() {
		return this.services;
	}

	public void setServices(final Collection<AcmeService> services) {
		this.services = services;
	}

}
