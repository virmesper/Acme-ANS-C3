
package acme.entities.S2;

import java.beans.Transient;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.S1.Flight;
import acme.entities.S1.FlightRepository;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booking extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
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

	// Derived attributes -----------------------------------------------------


	@Transient
	public Money getPrice() {
		Money result;
		FlightRepository flightRepository = SpringHelper.getBean(FlightRepository.class);
		BookingRepository bookingRepository = SpringHelper.getBean(BookingRepository.class);
		result = flightRepository.findCostByFlight(this.flightId.getId());
		Collection<Passenger> pg = bookingRepository.findPassengersByBooking(this.getId());
		Double amount = result.getAmount() * pg.size();
		result.setAmount(amount);
		return result;
	}
	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flightId;

	@Mandatory
	@Valid
	@ManyToOne
	private Customer	customer;

}
