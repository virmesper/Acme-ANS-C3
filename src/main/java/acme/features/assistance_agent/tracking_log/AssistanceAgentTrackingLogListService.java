
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Claim;
import acme.entities.student4.TrackingLog;
import acme.realms.assistance_agent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	private static final String							MASTER_ID	= "masterId";
	private final AssistanceAgentTrackingLogRepository	repository;


	@Autowired
	public AssistanceAgentTrackingLogListService(final AssistanceAgentTrackingLogRepository repository) {
		this.repository = repository;
	}

	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		AssistanceAgent assistanceAgent;

		masterId = super.getRequest().getData(AssistanceAgentTrackingLogListService.MASTER_ID, int.class);
		claim = this.repository.findOneClaimById(masterId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		status = claim != null && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> objects;
		int masterId;

		masterId = super.getRequest().getData(AssistanceAgentTrackingLogListService.MASTER_ID, int.class);
		objects = this.repository.findManyTrackingLogsClaimId(masterId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final TrackingLog object) {
		String published;
		Dataset dataset;

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "indicator");
		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> object) {
		int masterId;
		Claim claim;
		Boolean noMore;
		Boolean exceptionalCase;
		Boolean greatRealm;

		masterId = super.getRequest().getData(AssistanceAgentTrackingLogListService.MASTER_ID, int.class);
		claim = this.repository.findOneClaimById(masterId);

		exceptionalCase = this.repository.countTrackingLogsForExceptionalCase(masterId) == 1;
		noMore = this.repository.countTrackingLogsForExceptionalCase(masterId) == 0;

		greatRealm = super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

		super.getResponse().addGlobal(AssistanceAgentTrackingLogListService.MASTER_ID, masterId);
		super.getResponse().addGlobal("exceptionalCase", exceptionalCase);
		super.getResponse().addGlobal("noMore", noMore);
		super.getResponse().addGlobal("greatRealm", greatRealm);
	}

}
