
package acme.features.authenticated.customer.passenger;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {

	// Constants --------------------------------------------------------------

	private static final String					DRAFT_MODE	= "draftMode";

	// Internal state ---------------------------------------------------------

	private final CustomerPassengerRepository	repository;

	// Constructors -----------------------------------------------------------


	public CustomerPassengerUpdateService(final CustomerPassengerRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		boolean status = passenger.getCustomer().getUserAccount().getId() == customerId && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", CustomerPassengerUpdateService.DRAFT_MODE, "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		if (passenger.isDraftMode())
			super.state(true, CustomerPassengerUpdateService.DRAFT_MODE, "El pasajero está en modo borrador y no puede actualizarse.");
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", CustomerPassengerUpdateService.DRAFT_MODE, "specialNeeds");
		super.getResponse().addData(dataset);
	}
}
