
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingListService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Collection<Booking> bookings = this.repository.findBookingByCustomer(userAccountId);
		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;
		boolean showCreate;

		Dataset dataset;

		// Obtener el tag del vuelo asociado al booking
		String flightTag = "";
		if (booking.getFlightId() != null)
			flightTag = booking.getFlightId().getTag();

		boolean isPublished = booking.isDraftMode();

		dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode");
		dataset.put("flightTag", flightTag);

		showCreate = super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !isPublished;

		super.getResponse().addGlobal("showCreate", showCreate);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}
