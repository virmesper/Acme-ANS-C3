
package acme.features.technician.involvedIn;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S5.InvolvedIn;
import acme.realms.Technician;

@GuiController
public class TechnicianInvolvedInController extends AbstractGuiController<Technician, InvolvedIn> {

	@Autowired
	private TechnicianInvolvedInCreateService	createService;

	@Autowired
	private TechnicianInvolvedInDeleteService	deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}
}
