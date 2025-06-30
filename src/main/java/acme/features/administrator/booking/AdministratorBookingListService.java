
package acme.features.administrator.booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.SupportedCurrency;

@GuiService
public class AdministratorBookingListService extends AbstractGuiService<Administrator, Booking> {

	// Internal state ---------------------------------------------------------

	private final AdministratorBookingRepository repository;

	// Constructor ------------------------------------------------------------


	public AdministratorBookingListService(final AdministratorBookingRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Booking> bookings;

		bookings = this.repository.findPublishedBookings();

		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking bookings) {
		Dataset dataset;
		List<String> passengers = new ArrayList<>();
		this.repository.findPassengersByBookingId(bookings.getId()).forEach(e -> passengers.add(e.getFullName()));

		dataset = super.unbindObject(bookings, "locatorCode", "purchaseMoment", "travelClass", "price");
		dataset.put("defaultPrice", SupportedCurrency.convertToDefault(bookings.getPrice()));
		dataset.put("passengers", passengers.isEmpty() ? "N/A" : passengers);

		super.getResponse().addData(dataset);
	}

}
