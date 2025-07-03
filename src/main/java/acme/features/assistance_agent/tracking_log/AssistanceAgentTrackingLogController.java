
package acme.features.assistance_agent.tracking_log;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student4.TrackingLog;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiController
public class AssistanceAgentTrackingLogController extends AbstractGuiController<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private final AssistanceAgentTrackingLogListService						listService;
	private final AssistanceAgentTrackingLogShowService						showService;
	private final AssistanceAgentTrackingLogCreateService					createService;
	private final AssistanceAgentTrackingLogUpdateService					updateService;
	private final AssistanceAgentTrackingLogDeleteService					deleteService;
	private final AssistanceAgentTrackingLogPublishService					publishService;
	private final AssistanceAgentTrackingLogCreateExceptionalCaseService	createExceptionalCaseService;


	@Autowired
	public AssistanceAgentTrackingLogController(final AssistanceAgentTrackingLogListService listService, final AssistanceAgentTrackingLogShowService showService, final AssistanceAgentTrackingLogCreateService createService,
		final AssistanceAgentTrackingLogUpdateService updateService, final AssistanceAgentTrackingLogDeleteService deleteService, final AssistanceAgentTrackingLogPublishService publishService,
		final AssistanceAgentTrackingLogCreateExceptionalCaseService createExceptionalCaseService) {

		this.listService = listService;
		this.showService = showService;
		this.createService = createService;
		this.updateService = updateService;
		this.deleteService = deleteService;
		this.publishService = publishService;
		this.createExceptionalCaseService = createExceptionalCaseService;
	}

	// Constructors -----------------------------------------------------------

	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addCustomCommand("exceptional-case", "create", this.createExceptionalCaseService);
	}

}
