
package acme.entities.group;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BannedPassenger extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				fullName;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dateOfBirth;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,9}$")
	@Automapped
	private String				passportNumber;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				nationality;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	private String				reasonForBan;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				banIssuedDate; //fecha prohibicion

	@Optional
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				liftDate; //fehca de levantamiento de la prohibicion
}
