
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Airline;
import acme.entities.group.AirlineType;

@GuiService
public class AdministratorAirlineShowService extends AbstractGuiService<Administrator, Airline> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository ar;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Airline airline;
		int airlineId;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.ar.findAirlineById(airlineId);

		super.getBuffer().addData(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		var typeChoices = SelectChoices.from(AirlineType.class, airline.getType());
		var airports = this.ar.findAllAirports();
		var airportChoices = SelectChoices.from(airports, "name", airline.getAirport());

		Dataset dataset = super.unbindObject(airline, "name", "iataCode", "website", "foundationMoment", "email", "phoneNumber");
		dataset.put("type", typeChoices.getSelected().getKey());
		dataset.put("airlineTypes", typeChoices);
		dataset.put("airport", airportChoices.getSelected().getKey());
		dataset.put("airports", airportChoices);

		super.getResponse().addData(dataset);
	}

}
