
package acme.entities.S3;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.Past;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import acme.realms.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightAssignment extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Automapped
	@Mandatory
	@Enumerated(EnumType.STRING)
	private DutyRole			dutyRole;

	@Automapped
	@Mandatory
	@Past
	private LocalDateTime		lastUpdate;

	@Automapped
	@Mandatory
	@Enumerated(EnumType.STRING)
	private AssignmentStatus	assignmentStatus;

	@Automapped
	@Optional
	@ValidString(max = 255)
	private String				remarks;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Automapped
	@Mandatory
	@Valid
	@ManyToOne
	private FlightCrewMember	flightCrewMember;
}
