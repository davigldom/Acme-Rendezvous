
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Category extends DomainEntity {

	private String	name;
	private String	description;

	@NotBlank
	@Column(unique=true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@NotBlank
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	//Relationships
	private Category parent;
	private Collection<AcmeService> services;


	@NotNull
	@Valid
	@OneToMany(mappedBy = "category")
	public Collection<AcmeService> getServices() {
		return services;
	}
	public void setServices(Collection<AcmeService> services) {
		this.services = services;
	}

	@Valid
	@ManyToOne(optional = true)
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}



}
