
package acme.entities.S3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Moment;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.realms.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityLog extends AbstractEntity {

	// Serialisation version-------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Moment				registrationMoment;

	@Mandatory
	@ValidString(max = 50)
	@Column(nullable = false)
	private String				incidentType;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@ValidNumber(max = 10, min = 0)
	@Automapped
	@Column(nullable = false)
	private Integer				severityLevel;

	// Relationships
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private FlightCrewMember	flightCrewMember;
}
