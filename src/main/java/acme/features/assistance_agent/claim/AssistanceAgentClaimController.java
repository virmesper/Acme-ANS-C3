
package acme.features.assistance_agent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student4.Claim;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentClaimListUndergoingService	listUndergoingService;
	private final AssistanceAgentClaimListCompletedService	listCompletedService;
	private final AssistanceAgentClaimShowService			showService;
	private final AssistanceAgentClaimCreateService			createService;
	private final AssistanceAgentClaimDeleteService			deleteService;
	private final AssistanceAgentClaimUpdateService			updateService;
	private final AssistanceAgentClaimPublishService		publishService;


	@Autowired
	public AssistanceAgentClaimController(final AssistanceAgentClaimListUndergoingService listUndergoingService, final AssistanceAgentClaimListCompletedService listCompletedService, final AssistanceAgentClaimShowService showService,
		final AssistanceAgentClaimCreateService createService, final AssistanceAgentClaimDeleteService deleteService, final AssistanceAgentClaimUpdateService updateService, final AssistanceAgentClaimPublishService publishService) {

		this.listUndergoingService = listUndergoingService;
		this.listCompletedService = listCompletedService;
		this.showService = showService;
		this.createService = createService;
		this.deleteService = deleteService;
		this.updateService = updateService;
		this.publishService = publishService;
	}

	// Constructors -----------------------------------------------------------

	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-undergoing", "list", this.listUndergoingService);
		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("publish", "update", this.publishService);
	}

}
