
package acme.features.administrator.airline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Airline;
import acme.entities.group.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

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
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");

		// airport desde el formulario
		if (super.getRequest().hasData("airport", int.class)) {
			int airportId = super.getRequest().getData("airport", int.class);
			airline.setAirport(this.ar.findAirportById(airportId));
		}
	}

	@Override
	public void unbind(final Airline airline) {
		var typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		var airports = this.ar.findAllAirports();
		var airportChoices = SelectChoices.from(airports, "name", airline.getAirport());

		Dataset dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");

		dataset.put("type", typeChoices.getSelected().getKey());
		dataset.put("airlineTypes", typeChoices);

		dataset.put("airport", airportChoices.getSelected().getKey());
		dataset.put("airports", airportChoices);

		dataset.put("confirmation", false);
		super.getResponse().addData(dataset);
	}

	@Override
	public void validate(final Airline airline) {
		List<Airline> airlines = this.ar.findAllAirlines();
		List<String> airlineIds = airlines.stream().filter(a -> a.getId() != airline.getId()).map(Airline::getIataCode).toList();

		if (airline.getIataCode() != null)
			super.state(!airlineIds.contains(airline.getIataCode()), "iataCode", "administrator.airline.create.not-unique-iata");

		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

	}

	@Override
	public void perform(final Airline airline) {
		this.ar.save(airline);
	}

}
