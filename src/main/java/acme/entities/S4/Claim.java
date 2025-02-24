
package acme.entities.S4;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "claims")

public class Claim extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Past
	@NotNull
	@Column(nullable = false)
	private LocalDateTime		registrationMoment;

	@Email
	@NotBlank
	@Column(length = 100, nullable = false)
	private String				passengerEmail;

	@NotBlank
	@Size(max = 255)
	@Column(length = 255, nullable = false)
	private String				description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ClaimType			type;

	// Indica si la reclamación ha sido aceptada o no 
	@NotNull
	@Column(nullable = false)
	private Boolean				accepted;

	// Relationships ----------------------------------------------------------

	// Agente de asistencia que registró la reclamación 
	@ManyToOne(optional = false)
	@JoinColumn(name = "assistance_agent_id", nullable = false)
	private AssistanceAgent		registeredBy;

}
