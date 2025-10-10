
package acme.features.authenticated.customer.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.forms.CustomerDashboard;
import acme.realms.Customer;

@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	@Autowired
	private CustomerDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {
		final int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		final Date now = new Date();
		final Calendar cal = Calendar.getInstance();

		cal.setTime(now);
		cal.add(Calendar.YEAR, -1);
		final Date oneYearAgo = cal.getTime();

		cal.setTime(now);
		cal.add(Calendar.YEAR, -5);
		final Date fiveYearsAgo = cal.getTime();

		final CustomerDashboard vm = new CustomerDashboard();

		// 1) Last five destinations (usando @Transient getDestinationCity())
		final List<Booking> ordered = new ArrayList<>(this.repository.findBookingsOrderedByPurchaseDesc(customerId));
		final List<Booking> top5 = ordered.size() > 5 ? ordered.subList(0, 5) : ordered;

		final List<String> lastFiveDestinations = top5.stream().map(b -> b.getFlightId() != null ? b.getFlightId().getDestinationCity() : "").collect(Collectors.toList());
		vm.setLastFiveDestinations(lastFiveDestinations);

		// 2) Dinero gastado último año
		final Double spent = this.repository.sumMoneySpentSince(customerId, oneYearAgo);
		vm.setMoneySpentLastYear(spent != null ? spent : 0.0);

		// 3) Nº de bookings por travel class
		final Map<String, Long> byClass = new LinkedHashMap<>();
		for (Object[] row : this.repository.countBookingsByTravelClass(customerId))
			byClass.put((String) row[0], (Long) row[1]);
		vm.setBookingsByTravelClass(byClass);

		// 4) Stats coste 5 años
		final Collection<Double> amounts = this.repository.findBookingAmountsSince(customerId, fiveYearsAgo);
		vm.setCostCount5y((long) amounts.size());
		if (amounts.isEmpty()) {
			vm.setCostAvg5y(0.0);
			vm.setCostMin5y(0.0);
			vm.setCostMax5y(0.0);
			vm.setCostStddev5y(0.0);
		} else {
			final double min = amounts.stream().min(Double::compare).get();
			final double max = amounts.stream().max(Double::compare).get();
			final double sum = amounts.stream().mapToDouble(Double::doubleValue).sum();
			final double avg = sum / amounts.size();
			final double var = amounts.stream().mapToDouble(x -> (x - avg) * (x - avg)).sum() / amounts.size();
			vm.setCostAvg5y(avg);
			vm.setCostMin5y(min);
			vm.setCostMax5y(max);
			vm.setCostStddev5y(Math.sqrt(var));
		}

		// 5) Stats nº pasajeros por booking
		final Collection<Long> paxCounts = this.repository.passengersPerBookingCounts(customerId);
		vm.setPaxCountSamples((long) paxCounts.size());
		if (paxCounts.isEmpty()) {
			vm.setPaxAvg(0.0);
			vm.setPaxMin(0L);
			vm.setPaxMax(0L);
			vm.setPaxStddev(0.0);
		} else {
			final long min = paxCounts.stream().min(Long::compare).get();
			final long max = paxCounts.stream().max(Long::compare).get();
			final double sum = paxCounts.stream().mapToDouble(Long::doubleValue).sum();
			final double avg = sum / paxCounts.size();
			final double var = paxCounts.stream().mapToDouble(x -> (x - avg) * (x - avg)).sum() / paxCounts.size();
			vm.setPaxAvg(avg);
			vm.setPaxMin(min);
			vm.setPaxMax(max);
			vm.setPaxStddev(Math.sqrt(var));
		}

		super.getBuffer().addData(vm);
	}

	@Override
	public void unbind(final CustomerDashboard vm) {
		final Dataset ds = new Dataset();
		ds.put("lastFiveDestinations", vm.getLastFiveDestinations());
		ds.put("moneySpentLastYear", vm.getMoneySpentLastYear());
		ds.put("bookingsByTravelClass", vm.getBookingsByTravelClass());
		ds.put("costCount5y", vm.getCostCount5y());
		ds.put("costAvg5y", vm.getCostAvg5y());
		ds.put("costMin5y", vm.getCostMin5y());
		ds.put("costMax5y", vm.getCostMax5y());
		ds.put("costStddev5y", vm.getCostStddev5y());
		ds.put("paxCountSamples", vm.getPaxCountSamples());
		ds.put("paxAvg", vm.getPaxAvg());
		ds.put("paxMin", vm.getPaxMin());
		ds.put("paxMax", vm.getPaxMax());
		ds.put("paxStddev", vm.getPaxStddev());
		super.getResponse().addData(ds);
	}
}
