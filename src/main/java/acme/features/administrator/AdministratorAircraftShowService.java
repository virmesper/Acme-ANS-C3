
package acme.features.administrator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Group.Aircraft;
import acme.entities.Group.Airline;
import acme.entities.Group.Status;

@GuiService
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
		SelectChoices selectedAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		SelectChoices statuses = SelectChoices.from(Status.class, aircraft.getStatus());
		Dataset dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("airlines", selectedAirlines);
		dataset.put("statuses", statuses);
		dataset.put("confirmation", false);
		super.getResponse().addData(dataset);
	}
}
