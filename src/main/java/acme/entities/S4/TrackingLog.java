
package acme.entities.S4;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TrackingLog extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	// Paso actual del procedimiento
	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				currentStep;

	@Mandatory
	@ValidNumber(min = 0, max = 100)
	@Automapped
	private Integer				resolutionPercentage;

	@Mandatory
	@Automapped
	private Boolean				finalDecision;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				resolutionDetails;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private Claim				claim; // Relación con la reclamación a la que pertenece
}
