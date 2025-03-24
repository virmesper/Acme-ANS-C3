
package acme.features.authenticated.management;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.realms.Manager;

@GuiController
public class FlightAssignmentController extends AbstractGuiController<Authenticated, Manager> {
	//Internal state ---------------------------------------------------------

	@Autowired

	private FlightAssignmentService listService;

	//Constructors ---------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("listCompleted", this.listService);
		super.addBasicCommand("listPlanned", this.listService);
	}
}
