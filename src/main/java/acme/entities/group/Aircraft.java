
package acme.entities.group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.constraints.ValidAircraft;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import acme.constraints.ValidUniqueRegistrationNumber;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidAircraft
@Table(indexes = {
	@Index(columnList = "registrationNumber"),//
	@Index(columnList = "status")
})
public class Aircraft extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidShortText
	@Automapped
	private String				model;

	@Mandatory
	@ValidShortText
	@Column(unique = true)
	@ValidUniqueRegistrationNumber
	private String				registrationNumber;

	@Mandatory
	@ValidNumber(min = 1, max = 255)
	@Automapped
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	@Automapped
	private Double				cargoWeight;

	@Mandatory
	@Valid
	@Automapped
	private Status				status;

	@Optional
	@ValidLongText
	@Automapped
	private String				details;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
	@Mandatory
	@Valid
	@ManyToOne
	private Airline				airline;
}
