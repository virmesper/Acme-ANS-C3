
package acme.features.authenticated.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id;
		Passenger passenger;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		// VALIDACIÓN FUERTE
		if (passenger == null)
			throw new IllegalArgumentException("Passenger no encontrado con ID: " + id);

		boolean status = passenger.getCustomer().getUserAccount().getId() == customerId && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id;
		Passenger passenger;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		// VALIDACIÓN FUERTE
		if (passenger == null)
			throw new IllegalArgumentException("Passenger no encontrado con ID: " + id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "draftMode", "specialNeeds");

		super.getResponse().addData(dataset);
	}
}
