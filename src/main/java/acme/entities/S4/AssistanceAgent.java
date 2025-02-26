
package acme.entities.S4;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.entities.Group.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AssistanceAgent extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", min = 8, max = 9)
	@Column(unique = true, nullable = false, length = 9)
	private String				employeeCode;

	@Mandatory
	@ValidString(max = 255) // Limita la longitud total
	@Column(nullable = false, length = 255) // En BD, se guarda como un campo de texto
	private String				spokenLanguages;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date				startDate;

	@Optional
	@ValidString(max = 255)
	@Column(length = 255)
	private String				bio;

	@Optional
	@ValidMoney(min = 0)
	private Money				salary;

	@Optional
	@ValidUrl
	@Column(length = 500) // URLs largas pueden ser necesarias
	private String				photoUrl;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline; // Relación con Airline
}
