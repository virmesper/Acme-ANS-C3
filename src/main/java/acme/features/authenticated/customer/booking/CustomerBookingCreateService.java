
package acme.features.authenticated.customer.booking;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Flight;
import acme.entities.S2.Booking;
import acme.entities.S2.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);
	}

	@Override
	public void load() {
		Booking object = new Booking();
		object.setDraftMode(false);
		super.getBuffer().addData(object);
	}
	@Override
	public void bind(final Booking object) {
		assert object != null;

		// Verificar si estamos creando una reserva nueva
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			object.setPurchaseMoment(new Date());

		super.bindObject(object, "travelClass", "price", "locatorCode", "lastCardDigits");

		// Obtener el ID del vuelo desde el formulario
		int flightId = super.getRequest().getData("flightId", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		object.setFlightId(flight);
	}

	@Override
	public void validate(final Booking object) {
		assert object != null;
		// Puedes dejarlo vacío por ahora
	}

	@Override
	public void perform(final Booking object) {
		assert object != null;

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int customerId = this.repository.findCustomerIdByUserId(userAccountId);

		Customer customer = new Customer();
		customer.setId(customerId);

		object.setCustomer(customer);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Booking object) {
		assert object != null;

		SelectChoices flights = SelectChoices.from(this.repository.findAllFlights(), "id", object.getFlightId());
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, object.getTravelClass());

		Dataset dataset = super.unbindObject(object, "travelClass", "price", "locatorCode", "lastCardDigits");
		dataset.put("flights", flights);
		dataset.put("travelClasses", travelClasses);
		dataset.put("bookingId", object.getId());  // Aseguramos el ID en el dataset
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
