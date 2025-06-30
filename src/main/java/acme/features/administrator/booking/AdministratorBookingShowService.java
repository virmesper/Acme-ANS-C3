
package acme.features.administrator.booking;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Flight;
import acme.entities.student2.Booking;

@GuiService
public class AdministratorBookingShowService extends AbstractGuiService<Administrator, Booking> {

	// Internal state ---------------------------------------------------------

	private final AdministratorBookingRepository repository;

	// Constructor ------------------------------------------------------------


	public AdministratorBookingShowService(final AdministratorBookingRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		status = booking != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && !booking.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;

		Flight flight = this.repository.findFlightById(booking.getFlightId().getId());

		String origin = flight.getTag();
		Money cost = flight.getCost();

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "lastCardDigits");
		dataset.put("flight", String.format("%s - %s ", origin, cost));

		super.getResponse().addData(dataset);
	}

}
