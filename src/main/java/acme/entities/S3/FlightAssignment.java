
package acme.entities.S3;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.S1.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "draftMode"), @Index(columnList = "leg_id"), @Index(columnList = "flight_crew_member_id"), @Index(columnList = "leg_id, duty"), @Index(columnList = "flight_crew_member_id, leg_id"), @Index(columnList = "moment"),
	@Index(columnList = "currentStatus")
})
public class FlightAssignment extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private Duty				duty;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private CurrentStatus		currentStatus;

	@Optional
	@Automapped
	@ValidString(max = 255)
	private String				remarks;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@ManyToOne(optional = false)
	private FlightCrewMember	flightCrewMember;

	@Mandatory
	@ManyToOne(optional = false)
	private Leg					leg;

}
