
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.group.Airport;
import acme.entities.group.OperationalScope;

@GuiService
public class AdministratorAirportCreateService extends AbstractGuiService<Administrator, Airport> {

	private static final String				CONFIRMATION	= "confirmation";

	@Autowired
	private AdministratorAirportRepository	repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airport airport = new Airport();
		airport.setName("");
		airport.setIataCode("");
		airport.setOperationalScope(OperationalScope.DOMESTIC);
		airport.setCity("");
		airport.setCountry("");
		airport.setWebsite("");
		airport.setPhoneNumber("");

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "city", "country", "website", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation = super.getRequest().getData(AdministratorAirportCreateService.CONFIRMATION, boolean.class);
		super.state(confirmation, AdministratorAirportCreateService.CONFIRMATION, "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		SelectChoices choices = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());

		Dataset dataset = super.unbindObject(airport, "name", "iataCode", "city", "country", "website", "email", "phoneNumber");
		dataset.put("operationalScopes", choices);
		dataset.put(AdministratorAirportCreateService.CONFIRMATION, false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}
}
