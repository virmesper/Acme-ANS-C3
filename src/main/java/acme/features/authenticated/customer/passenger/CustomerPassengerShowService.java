
package acme.features.authenticated.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S2.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int passengerId;
		int userId;
		int customerId;
		Passenger passenger;

		userId = super.getRequest().getPrincipal().getAccountId();
		customerId = this.repository.findCustomerIdByUserId(userId);
		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findById(passengerId);

		status = passenger != null && customerId == passenger.getCustomer().getId();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findById(passengerId);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {

		assert passenger != null;

		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email", "dateOfBirth");

		super.getResponse().addData(dataset);
	}
}
