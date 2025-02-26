
package acme.entities.Group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	private String				model;

	@Mandatory
	@ValidString(max = 50)
	@Column(unique = true)
	private String				registrationnumber;

	@Mandatory
	@ValidNumber
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	private Double				cargoweight;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Valid
	private Status				status;

	@Optional
	@ValidString(max = 255)
	private String				details;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
