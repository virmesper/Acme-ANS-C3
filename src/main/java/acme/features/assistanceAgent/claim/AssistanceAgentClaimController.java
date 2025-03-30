
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S4.Claim;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimListUndergoingService	listUndergoingService;

	@Autowired
	private AssistanceAgentUndergoingClaimShowService	undergoingClaimShowService;

	@Autowired
	private AssistanceAgentClaimListCompletedService	listCompletedService;

	@Autowired
	private AssistanceAgentCompletedClaimShowService	completedClaimShowService;

	@Autowired
	private AssistanceAgentClaimCreateService			createService;

	@Autowired
	private AssistanceAgentClaimDeleteService			deleteService;

	@Autowired
	private AssistanceAgentClaimUpdateService			updateService;

	@Autowired
	private AssistanceAgentClaimPublishService			publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-undergoing", "list", this.listUndergoingService);
		super.addCustomCommand("show-undergoing", "show", this.undergoingClaimShowService);
		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("show-completed", "show", this.completedClaimShowService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("publish", "update", this.publishService);
	}

}
