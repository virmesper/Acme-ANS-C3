
package acme.entities.student2;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidUniqueLocatorCode;
import acme.entities.S1.Flight;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "locatorCode", unique = true), @Index(columnList = "flight_id"), @Index(columnList = "customer_id"), @Index(columnList = "draftMode"),
})
public class Booking extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_id")
	private Flight				flightId;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	@ValidUniqueLocatorCode
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidString(pattern = "^[0-9]{4}$")
	@Automapped
	private String				lastCardDigits;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Mandatory
	@ValidMoney(min = 0.00, max = 999999999999.99)
	@Automapped
	private Money				price;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private Customer			customer;

}
