
package acme.features.authenticated.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		int userId;
		int customerId;
		Booking booking;

		userId = super.getRequest().getPrincipal().getAccountId();
		customerId = this.repository.findCustomerIdByUserId(userId);
		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findById(bookingId);

		status = booking != null && customerId == booking.getCustomer().getId();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;

		Dataset dataset;
		SelectChoices flights;
		SelectChoices travelClasses;

		// Mostrar la ciudad de destino en lugar del número de vuelo
		flights = SelectChoices.from(this.repository.findAllFlights(), "tag", booking.getFlightId());
		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		// Crear el dataset con los datos de la reserva
		dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "flightId", "purchaseMoment", "lastCardDigits", "draftMode");
		dataset.put("travelClass", travelClasses);
		dataset.put("flights", flights);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}
