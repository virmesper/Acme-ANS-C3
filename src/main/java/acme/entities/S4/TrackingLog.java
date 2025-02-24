
package acme.entities.S4;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tracking_logs")

public class TrackingLog extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	// Momento de la última actualización del seguimiento 
	@PastOrPresent
	@NotNull
	@Column(nullable = false)
	private LocalDateTime		lastUpdateMoment;

	// Paso actual en el procedimiento de resolución
	@NotBlank
	@Size(max = 50)
	@Column(length = 50, nullable = false)
	private String				step;

	@Min(0)
	@Max(100)
	@Column(nullable = false)
	private Integer				resolutionPercentage;

	// Indica si el reclamo ha sido finalmente aceptado o no
	@NotNull
	@Column(nullable = false)
	private Boolean				finalDecision;

	// Motivo de rechazo o compensación ofrecida
	@Size(max = 255)
	@Column(length = 255, nullable = true)
	private String				resolutionDetail;

	// Relationships ----------------------------------------------------------

	// Reclamo al que pertenece este seguimiento 
	@ManyToOne(optional = false)
	@JoinColumn(name = "claim_id", nullable = false)
	private Claim				claim;
}
