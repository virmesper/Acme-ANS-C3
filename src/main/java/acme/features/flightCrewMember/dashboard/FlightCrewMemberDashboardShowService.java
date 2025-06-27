
package acme.features.flightCrewMember.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.FlightAssignment;
import acme.forms.FlightCrewMemberDashboard;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightCrewMemberDashboardShowService extends AbstractGuiService<FlightCrewMember, FlightCrewMemberDashboard> {

	@Autowired
	protected FlightCrewMemberDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMemberDashboard dashboard = new FlightCrewMemberDashboard();
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		// Últimos 5 destinos
		PageRequest p = PageRequest.of(0, 5);
		Page<String> page = this.repository.findDestinations(crewMemberId, p);
		List<String> lastFive = page.getContent();
		dashboard.setLastFiveDestinations(lastFive);

		// Logs por rango
		List<Object[]> rawRanges = this.repository.countLogsBySeverityRanges(crewMemberId);
		Map<String, Long> activityLogCounts = new HashMap<>();
		for (Object[] row : rawRanges) {
			String range = (String) row[0];
			Long count = (Long) row[1];
			activityLogCounts.put(range, count);
		}
		dashboard.setActivityLogCounts(activityLogCounts);

		// Última leg
		Date now = MomentHelper.getCurrentMoment();

		List<Integer> lastLegs = this.repository.findLastLegIdByCrewMember(crewMemberId, now);
		Integer lastLegId = lastLegs.isEmpty() ? null : lastLegs.get(0);
		// Compañeros de etapa
		List<FlightCrewMember> colleagues = lastLegId != null ? this.repository.findColleaguesByLegId(lastLegId, crewMemberId, now) : List.of();
		dashboard.setColleaguesInLastStage(colleagues);

		// Asignaciones agrupadas por status
		List<Object[]> rawAssignments = this.repository.findAssignmentsByStatus(crewMemberId);
		Map<CurrentStatus, List<FlightAssignment>> grouped = new HashMap<>();
		for (Object[] row : rawAssignments) {
			CurrentStatus st = (CurrentStatus) row[0];
			FlightAssignment fa = (FlightAssignment) row[1];
			grouped.computeIfAbsent(st, x -> new ArrayList<>()).add(fa);
		}
		dashboard.setAssignmentsByStatus(grouped);

		// Estadísticas

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_YEAR, -30);
		Date start = cal.getTime();

		List<Object[]> rawData = this.repository.findAssignmentsCountPerDayInLastMonth(crewMemberId, start, now);

		// Extraigo los conteos
		List<Long> counts = new ArrayList<>();
		for (Object[] row : rawData) {
			// row[0] es la fecha, row[1] es la cantidad
			Long count = (Long) row[1];
			counts.add(count);
		}
		if (counts.isEmpty()) {
			dashboard.setAverage(0.0);
			dashboard.setMinimum(0.0);
			dashboard.setMaximum(0.0);
			dashboard.setStandardDesviation(0.0);
		} else {
			// Calculo average, min, max, std dev de "counts"
			long min = Long.MAX_VALUE;
			long max = Long.MIN_VALUE;
			long sum = 0;
			for (Long c : counts) {
				if (c < min)
					min = c;
				if (c > max)
					max = c;
				sum += c;
			}
			double average = counts.isEmpty() ? 0 : (double) sum / counts.size();

			// std dev
			double variance = 0;
			for (Long c : counts) {
				double diff = c - average;
				variance += diff * diff;
			}
			double std = counts.size() > 1 ? Math.sqrt(variance / (counts.size() - 1)) : 0.0;

			dashboard.setAverage(average);
			dashboard.setMinimum((double) min);
			dashboard.setMaximum((double) max);
			dashboard.setStandardDesviation(std);
		}

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final FlightCrewMemberDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "activityLogCounts", "colleaguesInLastStage", "assignmentsByStatus", "average", "minimum", "maximum", "standardDesviation");

		super.getResponse().addData(dataset);
	}
}
