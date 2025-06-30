
package acme.features.authenticated.customer.dashboard;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.SupportedCurrency;
import acme.entities.student2.TravelClass;
import acme.forms.CustomerDashboard;
import acme.realms.Customer;

@SuppressWarnings("deprecation")
@GuiService
public class CustomerDashboardShowService extends AbstractGuiService<Customer, CustomerDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {
		Customer customer = (Customer) super.getRequest().getPrincipal().getRealmOfType(Customer.class);
		int customerId = customer.getId();
		CustomerDashboard dashboard = new CustomerDashboard();

		Collection<Booking> bookings = this.repository.findAllBookings(customerId);
		int numBookings = bookings.size();
		String defaultCurrency = SupportedCurrency.getDefaultCurrency();

		if (numBookings == 0)
			this.fillEmptyDashboard(dashboard, defaultCurrency);
		else
			this.fillDashboardWithData(dashboard, bookings, customerId, defaultCurrency);

		super.getBuffer().addData(dashboard);
	}

	private void fillEmptyDashboard(final CustomerDashboard dashboard, final String currency) {
		Money noMoney = new Money();
		noMoney.setCurrency(currency);
		noMoney.setAmount(0.0);

		dashboard.setLastFiveDestinations("N/A");
		dashboard.setMoneySpentLastYear(noMoney);
		dashboard.setNumOfBookingsByTravelClass("N/A");
		dashboard.setMaxCostOfBookingsLastFiveYears(noMoney);
		dashboard.setMinCostOfBookingsLastFiveYears(noMoney);
		dashboard.setAvgCostOfBookingsLastFiveYears(noMoney);
		dashboard.setStdDeviationCostOfBookingsLastFiveYears(0.0);
		dashboard.setMinNumOfPassengersInBookings(0);
		dashboard.setMaxNumOfPassengersInBookings(0);
		dashboard.setAvgNumOfPassengersInBookings(0.0);
		dashboard.setStdDeviationNumOfPassengersInBookings(0.0);
	}

	private void fillDashboardWithData(final CustomerDashboard dashboard, final Collection<Booking> bookings, final int customerId, final String defaultCurrency) {
		dashboard.setLastFiveDestinations(this.getLastFiveDestinations(bookings));
		dashboard.setMoneySpentLastYear(this.getMoneySpentLastYear(customerId, defaultCurrency));
		dashboard.setNumOfBookingsByTravelClass(this.getBookingsByTravelClassString(bookings));
		this.setCostStatistics(dashboard, bookings, defaultCurrency);
		this.setPassengerStatistics(dashboard, bookings);
	}

	private String getLastFiveDestinations(final Collection<Booking> bookings) {
		Comparator<Booking> cmp = Comparator.comparing(Booking::getPurchaseMoment).thenComparing(Booking::getId);
		List<String> lastDestiniesList = bookings.stream().sorted(cmp).map(b -> b.getFlightId().getDestinationCity()).distinct().limit(5L).toList();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lastDestiniesList.size(); i++)
			sb.append(i + 1).append(": ").append(lastDestiniesList.get(i)).append(", ");
		return sb.substring(0, sb.length() - 2);
	}

	private Money getMoneySpentLastYear(final int customerId, final String currency) {
		Date lastYear = MomentHelper.getCurrentMoment();
		lastYear.setYear(lastYear.getYear() - 1);
		Double spent = this.repository.moneySpentLastYear(lastYear, customerId);
		Money result = new Money();
		result.setCurrency(currency);
		result.setAmount(spent != null ? spent : 0.0);
		return result;
	}

	private String getBookingsByTravelClassString(final Collection<Booking> bookings) {
		Map<TravelClass, Integer> map = bookings.stream().collect(Collectors.groupingBy(Booking::getTravelClass, Collectors.summingInt(e -> 1)));
		return map.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(", "));
	}

	private void setCostStatistics(final CustomerDashboard dashboard, final Collection<Booking> bookings, final String currency) {
		Date lastFiveYears = MomentHelper.getCurrentMoment();
		lastFiveYears.setYear(lastFiveYears.getYear() - 5);

		List<Booking> recent = bookings.stream().filter(b -> b.getPurchaseMoment().after(lastFiveYears)).toList();
		int count = recent.size();

		Money max = new Money(), min = new Money(), avg = new Money();
		max.setCurrency(currency);
		min.setCurrency(currency);
		avg.setCurrency(currency);
		double stdDev = 0.0;

		if (count > 0) {
			DoubleSummaryStatistics stats = recent.stream().mapToDouble(b -> {
				Money price = b.getPrice();
				return price.getCurrency().equals(currency) ? price.getAmount() : SupportedCurrency.convertToDefault(price).getAmount();
			}).summaryStatistics();

			double avgVal = stats.getAverage();
			max.setAmount(stats.getMax());
			min.setAmount(stats.getMin());
			avg.setAmount(avgVal);

			for (Booking b : recent) {
				double price = b.getPrice().getCurrency().equals(currency) ? b.getPrice().getAmount() : SupportedCurrency.convertToDefault(b.getPrice()).getAmount();
				stdDev += Math.pow(price - avgVal, 2);
			}
			stdDev = Math.sqrt(stdDev / count);
		} else {
			max.setAmount(0.0);
			min.setAmount(0.0);
			avg.setAmount(0.0);
		}

		dashboard.setMaxCostOfBookingsLastFiveYears(max);
		dashboard.setMinCostOfBookingsLastFiveYears(min);
		dashboard.setAvgCostOfBookingsLastFiveYears(avg);
		dashboard.setStdDeviationCostOfBookingsLastFiveYears(stdDev);
	}

	private void setPassengerStatistics(final CustomerDashboard dashboard, final Collection<Booking> bookings) {
		IntSummaryStatistics stats = bookings.stream().mapToInt(b -> this.repository.findPassengersByBookingId(b.getId()).size()).summaryStatistics();
		double avg = stats.getAverage();
		double stdDev = bookings.stream().mapToDouble(b -> {
			int pCount = this.repository.findPassengersByBookingId(b.getId()).size();
			return Math.pow(pCount - avg, 2);
		}).sum();
		stdDev = Math.sqrt(stdDev / bookings.size());

		dashboard.setMinNumOfPassengersInBookings(stats.getMin());
		dashboard.setMaxNumOfPassengersInBookings(stats.getMax());
		dashboard.setAvgNumOfPassengersInBookings(avg);
		dashboard.setStdDeviationNumOfPassengersInBookings(stdDev);
	}

	@Override
	public void unbind(final CustomerDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "lastFiveDestinations", "moneySpentLastYear", "numOfBookingsByTravelClass", "avgCostOfBookingsLastFiveYears", "minCostOfBookingsLastFiveYears", "maxCostOfBookingsLastFiveYears",
			"stdDeviationCostOfBookingsLastFiveYears", "avgNumOfPassengersInBookings", "minNumOfPassengersInBookings", "maxNumOfPassengersInBookings", "stdDeviationNumOfPassengersInBookings");

		super.getResponse().addData(dataset);
	}
}
