
package acme.features.administrator.tracking_log;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student4.Indicator;
import acme.entities.student4.TrackingLog;

@GuiService
public class AdministratorTrackingLogShowService extends AbstractGuiService<Administrator, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private final AdministratorTrackingLogRepository repository;


	@Autowired
	public AdministratorTrackingLogShowService(final AdministratorTrackingLogRepository repository) {
		this.repository = repository;
	}

	// AbstractService interface ----------------------------------------------

	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findOneTrackingLogById(trackingLogId);
		status = trackingLog != null && !trackingLog.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog object;
		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTrackingLogById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final TrackingLog object) {
		Dataset dataset;

		SelectChoices choicesIndicator;

		choicesIndicator = SelectChoices.from(Indicator.class, object.getIndicator());

		dataset = super.unbindObject(object, "lastUpdateMoment", "step", "resolutionPercentage", "draftMode", "resolution", "indicator");
		dataset.put("indicators", choicesIndicator);

		super.getResponse().addData(dataset);
	}

}
