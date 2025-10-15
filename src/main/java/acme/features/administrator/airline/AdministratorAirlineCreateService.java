
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
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

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

		airline = new Airline();

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
		if (super.getRequest().hasData("airport", int.class)) {
			int airportId = super.getRequest().getData("airport", int.class);
			airline.setAirport(this.ar.findAirportById(airportId));
		}
	}

	@Override
	public void validate(final Airline airline) {
		// IATA único (mejor directo)
		if (!super.getBuffer().getErrors().hasErrors("iataCode") && airline.getIataCode() != null) {
			var existing = this.ar.findAirlineByIataCode(airline.getIataCode());
			super.state(existing == null, "iataCode", "administrator.airline.create.not-unique-iata");
		}

		// Airport obligatorio y válido
		if (!super.getBuffer().getErrors().hasErrors("airport")) {
			super.state(airline.getAirport() != null, "airport", "administrator.airline.form.error.airport-required");
			if (airline.getAirport() != null)
				super.state(this.ar.findAirportById(airline.getAirport().getId()) != null, "airport", "administrator.airline.form.error.airport-not-found");
		}

		// Confirmación
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.ar.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		var airports = this.ar.findAllAirports();
		SelectChoices airportChoices = SelectChoices.from(airports, "name", airline.getAirport());

		Dataset dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");

		dataset.put("type", typeChoices.getSelected().getKey());
		dataset.put("airlineTypes", typeChoices);

		dataset.put("airport", airportChoices.getSelected().getKey());
		dataset.put("airports", airportChoices);

		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}
}
