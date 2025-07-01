
package acme.realms.assistanceAgent;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAssistanceAgent;
import acme.entities.group.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity

@Table(indexes = {
	@Index(columnList = "airline_id"), @Index(columnList = "moment"), @Index(columnList = "spokenLanguages")
})
@Getter
@Setter
@ValidAssistanceAgent
public class AssistanceAgent extends AbstractRole {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				spokenLanguages;

	// Fecha en la que comenzó a trabajar para la aerolínea
	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				briefBio;

	@Optional
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				salary;

	@Optional
	@ValidUrl
	@Automapped
	private String				photoUrl;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	// Aerolínea para la que trabaja el agente
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;
}
