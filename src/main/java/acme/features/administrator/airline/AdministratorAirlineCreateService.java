
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Airline;
import acme.entities.group.AirlineType;
import acme.entities.group.Airport;
import acme.features.administrator.airport.AdministratorAirportRepository;

@GuiService
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

	private static final String				IATA_CODE_AIRPORT	= "iataCodeAirport";

	@Autowired
	private AdministratorAirlineRepository	repository;

	@Autowired
	private AdministratorAirportRepository	repositoryAirline;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airline airline = new Airline();
		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber", AdministratorAirlineCreateService.IATA_CODE_AIRPORT);

		String iataCodeAirport = super.getRequest().getData(AdministratorAirlineCreateService.IATA_CODE_AIRPORT, String.class);

		if (iataCodeAirport != null && !iataCodeAirport.isEmpty()) {
			Airport airport = this.repositoryAirline.findAirportByIataCode(iataCodeAirport);

			if (airport != null)
				airline.setAirport(airport);
			else
				super.state(false, AdministratorAirlineCreateService.IATA_CODE_AIRPORT, "acme.validation.airport.notfound.message");
		}
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		if (airline.getAirport() != null)
			this.repository.save(airline);
		else
			super.state(false, "airportIataCode", "acme.validation.airport.notfound.message");
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices = SelectChoices.from(AirlineType.class, airline.getType());

		Dataset dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}
}
