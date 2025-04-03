
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Group.Airline;
import acme.entities.Group.AirlineType;
import acme.entities.Group.Airport;
import acme.features.administrator.airport.AdministratorAirportRepository;

@GuiService
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository	repository;

	@Autowired
	private AdministratorAirportRepository	repositoryAirline;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airline airline;

		airline = new Airline();

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber", "iataCodeAirport");

		// Obtener el IATA Code del formulario (corresponde a un solo Airport)
		String iataCodeAirport = super.getRequest().getData("iataCodeAirport", String.class);

		if (iataCodeAirport != null && !iataCodeAirport.isEmpty()) {
			// Buscar el aeropuerto con ese IATA Code en la base de datos
			Airport airport = this.repositoryAirline.findAirportByIataCode(iataCodeAirport);

			if (airport != null)
				airline.setAirport(airport);
			else
				super.state(false, "iataCodeAirport", "acme.validation.airport.notfound.message");
		}
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		// Si el aeropuerto es válido (no es null), guardamos el airline
		if (airline.getAirport() != null)
			this.repository.save(airline);
		else
			super.state(false, "airportIataCode", "acme.validation.airport.notfound.message");
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}
}
