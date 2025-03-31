
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S2.BookingRecord;
import acme.entities.S2.Passenger;
import acme.features.authenticated.customer.bookingRecord.CustomerBookingRecordRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerDeleteService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository		repository;

	@Autowired
	private CustomerBookingRecordRepository	bookingRecordRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id;
		Passenger passenger;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);
		boolean status = passenger.getCustomer().getUserAccount().getId() == customerId && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;

		AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		int customerId = principal.getId();
		Customer customer = this.repository.findCustomerById(customerId);

		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setDraftMode(true);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "draftMode", "specialNeeds");

	}

	@Override
	public void validate(final Passenger passenger) {
		;
	}

	@Override
	public void perform(final Passenger passenger) {
		Collection<BookingRecord> br = this.repository.findBookingRecordByPassenger(passenger.getId());
		for (BookingRecord bookingRecord : br)
			this.bookingRecordRepository.delete(bookingRecord);
		this.repository.delete(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "draftMode", "specialNeeds");

		super.getResponse().addData(dataset);
	}

}
