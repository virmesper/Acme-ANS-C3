
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.entities.S2.Booking;
import acme.entities.S2.Passenger;
import acme.entities.S2.TravelClass;

@Service
public class AutehnticatedBookingShowService extends AbstractGuiService<Authenticated, Booking> {

	@Autowired
	private AuthenticatedBookingRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id;
		Booking booking;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<Passenger> passengersNumber = this.repository.findPassengersByBooking(booking.getId());
		Collection<String> passengers = passengersNumber.stream().map(x -> x.getFullName()).toList();

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "price", "lastCardDigits", "draftMode");
		dataset.put("travelClass", choices);
		dataset.put("passengers", passengers);

		super.getResponse().addData(dataset);
	}
}
