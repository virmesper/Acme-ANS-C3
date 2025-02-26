
package acme.entities.S4;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Claim extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true) // Fecha en el pasado
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Column(nullable = false, length = 320) // Longitud máxima de emails
	private String				passengerEmail;

	@Mandatory
	@ValidString(max = 255)
	@Column(nullable = false, length = 255)
	private String				description;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20) // Máximo 20 caracteres para evitar sobreuso en BD
	private ClaimType			type;

	@Mandatory
	@Column(nullable = false)
	private Boolean				isAccepted;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent		registeredBy; // Agente que registra la reclamación
}
