
package acme.entities.S4;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
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
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(max = 50)
	@Column(nullable = false, length = 50)
	private String				currentStep;

	@Mandatory
	@Min(0)
	@Max(100)
	@Column(nullable = false)
	private Integer				resolutionPercentage;

	@Mandatory
	@Column(nullable = false)
	private Boolean				finalDecision;

	@Mandatory
	@ValidString(max = 255)
	@Column(nullable = false, length = 255)
	private String				resolutionDetails;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Claim				claim; // Relación con la reclamación a la que pertenece
}
