
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S2.Passenger;
import acme.features.authenticated.customer.booking.CustomerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	bookingRepository;

	@Autowired
	private CustomerPassengerRepository	passengerRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int customerId;
		Collection<Passenger> passengers;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		passengers = this.passengerRepository.findPassengerByCustomer(customerId);
		status = passengers.stream().allMatch(b -> b.getCustomer().getUserAccount().getId() == customerId) && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int id;

		id = super.getRequest().getData("bookingId", int.class);
		super.getResponse().addGlobal("bookingId", id);
		passengers = this.bookingRepository.findPassengersByBooking(id);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "specialNeeds");

		super.getResponse().addData(dataset);
	}
}
