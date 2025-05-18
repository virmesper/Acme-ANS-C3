
package acme.entities.S3;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidActivityLog;
import acme.constraints.ValidShortText;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidActivityLog
@Table(indexes = {
	@Index(columnList = "draftMode"), @Index(columnList = "flight_assignment_id"), @Index(columnList = "registrationMoment"), @Index(columnList = "typeOfIncident"), @Index(columnList = "severityLevel")
})
public class ActivityLog extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@ValidMoment(past = true)
	private Date				registrationMoment;

	@Mandatory
	@Automapped
	@ValidShortText
	private String				typeOfIncident;

	@Mandatory
	@Automapped
	@ValidString(min = 1, max = 255)
	private String				description;

	@Mandatory
	@Automapped
	@ValidNumber(min = 0, max = 10)
	private Integer				severityLevel;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private FlightAssignment	flightAssignment;

}
