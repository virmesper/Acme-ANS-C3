
package acme.features.flightcrewmember.activitylog;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import acme.client.controllers.AbstractController;
import acme.client.services.AbstractService;
import acme.entities.S3.ActivityLog;
import acme.internals.controllers.ControllerMetadata;
import acme.realms.FlightCrewMember;

@RestController
@RequestMapping("/flightcrewmembers/activitylog/")
public class FlightCrewMemberActivityLogController extends AbstractController<FlightCrewMember, ActivityLog> {

	@Autowired
	protected FlightCrewMemberActivityLogListService	listService;

	@Autowired
	protected FlightCrewMemberActivityLogShowService	showService;

	@Autowired
	protected FlightCrewMemberActivityLogCreateService	createService;

	@Autowired
	protected FlightCrewMemberActivityLogUpdateService	updateService;

	@Autowired
	protected FlightCrewMemberActivityLogDeleteService	deleteService;

	@Autowired
	protected FlightCrewMemberActivityLogPublishService	publishService;


	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

	@Override
	protected ControllerMetadata<FlightCrewMember, ActivityLog> buildMetadata(final Class<?> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RequestMappingInfo buildMappingInfo(final String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Method buildHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void redirect(final AbstractService<FlightCrewMember, ActivityLog> service) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object buildResult(final AbstractService<FlightCrewMember, ActivityLog> service) {
		// TODO Auto-generated method stub
		return null;
	}

}
