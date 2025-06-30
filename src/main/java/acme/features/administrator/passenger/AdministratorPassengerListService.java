
package acme.features.administrator.passenger;

import java.util.Collection;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.Passenger;

@GuiService
public class AdministratorPassengerListService extends AbstractGuiService<Administrator, Passenger> {

	// Internal state ---------------------------------------------------------

	private final AdministratorPassengerRepository repository;

	// Constructor ------------------------------------------------------------


	public AdministratorPassengerListService(final AdministratorPassengerRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData(AdministratorPassengerController.MASTER_ID, int.class);
		booking = this.repository.findBookingById(bookingId);
		status = booking != null && super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && !booking.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		Integer bookingId;

		bookingId = super.getRequest().getData(AdministratorPassengerController.MASTER_ID, int.class);
		passengers = this.repository.findPassengersByBookingId(bookingId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passengers) {
		Dataset dataset;
		String specialNeeds = passengers.getSpecialNeeds();

		dataset = super.unbindObject(passengers, "fullName", "email", "passportNumber", "dateOfBirth");
		dataset.put("specialNeeds", specialNeeds.isBlank() ? "N/A" : specialNeeds);

		super.getResponse().addData(dataset);
	}

}
