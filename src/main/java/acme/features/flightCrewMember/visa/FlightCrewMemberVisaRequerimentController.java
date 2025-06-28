
package acme.features.flightCrewMember.visa;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S3.VisaRequirement;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class FlightCrewMemberVisaRequerimentController extends AbstractGuiController<FlightCrewMember, VisaRequirement> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberVisaRequirementListService	listService;

	@Autowired
	private FlightCrewMemberVisaRequirementShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
