
package acme.entities.Group;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Airline extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Column(nullable = false)
	private String				name;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2}X$", min = 3, max = 3)
	@Column(unique = true, nullable = false)
	private String				iataCode;

	@Mandatory
	@ValidUrl
	@Column(nullable = false)
	private String				website;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private AirlineType			type;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date				foundationMoment;

	@Optional
	@ValidEmail
	@Column(length = 320)
	private String				email;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Column(length = 15)
	private String				phoneNumber;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
