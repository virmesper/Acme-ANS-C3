
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
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Aircraft aircraft;

		status = super.getRequest().hasData("id", int.class) ? true : false;

		if (status) {
			masterId = super.getRequest().getData("id", int.class);
			aircraft = this.repository.findAircraftById(masterId);
			status = aircraft != null;
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.repository.findAircraftById(id);
		super.getBuffer().addData(aircraft);
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
		dataset.put("statusString", aircraft.getStatus().name());

		super.getResponse().addData(dataset);
	}

}
