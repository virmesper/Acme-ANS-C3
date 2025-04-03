
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S4.Claim;
import acme.entities.S4.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		AssistanceAgent assistanceAgent;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findOneClaimById(masterId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		status = claim != null && super.getRequest().getPrincipal().hasRealm(assistanceAgent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> objects;
		int masterId;

		masterId = super.getRequest().getPrincipal().getActiveRealm().getId();
		objects = this.repository.findManyTrackingLogsByMasterId(masterId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		String published;
		Dataset dataset;
		int masterId;
		Claim claim;
		boolean exceptionalCase;
		boolean notAnyMore;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findOneClaimById(masterId);
		exceptionalCase = this.repository.countTrackingLogsForExceptionalCase(masterId) == 1;
		notAnyMore = this.repository.countTrackingLogsForExceptionalCase(masterId) == 2;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "indicator");
		published = !trackingLog.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("exceptionalCase", !claim.isDraftMode() && exceptionalCase);
		super.getResponse().addGlobal("notAnyMore", notAnyMore);

		super.getResponse().addData(dataset);
	}

}
