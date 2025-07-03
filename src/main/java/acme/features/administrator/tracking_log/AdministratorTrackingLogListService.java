
package acme.features.administrator.tracking_log;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.TrackingLog;

@GuiService
public class AdministratorTrackingLogListService extends AbstractGuiService<Administrator, TrackingLog> {

	// Internal staTrackingLog-------------------------------------------------------

	private final AdministratorTrackingLogRepository repository;


	@Autowired
	public AdministratorTrackingLogListService(final AdministratorTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------

	@Override
	public void authorise() {
		super.getResponse().setAuthorised("GET".equals(super.getRequest().getMethod()));
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;

		int claimId = super.getRequest().getData("claimId", int.class);
		trackingLogs = this.repository.findTrackingLogsPublishedByClaimId(claimId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog object) {
		String published;
		Dataset dataset;

		dataset = super.unbindObject(object, "lastUpdateMoment", "resolutionPercentage", "indicator");
		published = !object.isDraftMode() ? "✓" : "x";
		dataset.put("published", published);

		super.getResponse().addData(dataset);
	}

}
