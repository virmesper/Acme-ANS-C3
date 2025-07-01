
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
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		Aircraft aircraft;
		int aircraftId;
		boolean status;

		if (super.getRequest().hasData("id", int.class)) {
			aircraftId = super.getRequest().getData("id", int.class);
			aircraft = this.repository.findAircraftById(aircraftId);
			status = aircraft != null;
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int aircraftId;

		aircraftId = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {

		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {

		Collection<Airline> airlines = this.repository.findAllAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "name", aircraft.getAirline());

		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(Status.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		dataset.put("airlines", airlineChoices);
		dataset.put("statusString", aircraft.getStatus().name());

		super.getResponse().addData(dataset);
	}
}
