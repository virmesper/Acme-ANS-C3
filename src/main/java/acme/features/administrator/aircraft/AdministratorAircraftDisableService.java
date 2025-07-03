
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Aircraft;
import acme.entities.group.Airline;
import acme.entities.group.Status;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft aircraft = (Aircraft) this.repository.findById(id).get();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		String statusString = super.getRequest().getData("statusString", String.class);
		if ("ACTIVE_SERVICE".equals(statusString))
			aircraft.setStatus(Status.UNDER_MAINTENANCE);
		else if ("UNDER_MAINTENANCE".equals(statusString))
			aircraft.setStatus(Status.ACTIVE_SERVICE);
		this.repository.save(aircraft);  // Guarda el cambio de estado
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "name", aircraft.getAirline());

		SelectChoices statusChoices = SelectChoices.from(Status.class, aircraft.getStatus());

		Dataset dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

		dataset.put("airline", airlineChoices.getSelected().getKey());
		dataset.put("airlines", airlineChoices);

		dataset.put("status", statusChoices.getSelected().getKey());
		dataset.put("statuses", statusChoices);

		dataset.put("confirmation", false);
		dataset.put("statusString", aircraft.getStatus().name()); // Si lo usas en el formulario

		super.getResponse().addData(dataset);
	}

}
