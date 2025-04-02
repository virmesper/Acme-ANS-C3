
package acme.features.authenticated.management;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Flight;
import acme.realms.Manager;

@GuiService
public class FlightAssignmentService extends AbstractGuiService<Authenticated, Manager> {
	//Internal state ---------------------------------------------------------

	@Autowired

	private FlightAssignmentRepository repository;

	//AbstractGuiService interface ---------------------------------------------------------


	@Override

	public void authorise() {

		boolean status;
		int identifier;
		Flight flight;
		identifier = super.getRequest().getPrincipal().getAccountId();
		flight = this.repository.findFlightByAirlineManagerId(identifier);
		status = flight != null && super.getRequest().getPrincipal().hasRealmOfType(Manager.class);
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Flight manager;
		int id;
		id = super.getRequest().getPrincipal().getAccountId();
		manager = this.repository.findFlightByAirlineManagerId(id);
		super.getBuffer().addData(manager);

	}
}
