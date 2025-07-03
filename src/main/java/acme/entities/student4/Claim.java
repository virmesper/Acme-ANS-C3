
package acme.entities.student4;

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
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidClaim;
import acme.entities.student1.Leg;
<<<<<<< HEAD:src/main/java/acme/entities/S4/Claim.java
import acme.realms.assistanceAgent.AssistanceAgent;
=======
import acme.realms.assistance_agent.AssistanceAgent;
>>>>>>> master:src/main/java/acme/entities/student4/Claim.java
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(indexes = {
	@Index(columnList = "assistance_agent_id, indicator"), @Index(columnList = "leg_id"), @Index(name = "idx_claim_draft_regmoment", columnList = "draftMode, registrationMoment")
})
@Getter
@Setter
@ValidClaim
public class Claim extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Valid
	@Automapped
	private Indicator			indicator;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent		assistanceAgent; // Agente que registra la reclamación

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;
}
