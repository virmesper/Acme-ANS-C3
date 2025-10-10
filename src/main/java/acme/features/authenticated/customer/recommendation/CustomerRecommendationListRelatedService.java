
package acme.features.authenticated.customer.recommendation;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Booking;
import acme.entities.student2.Recommendation;
import acme.realms.Customer;

@GuiService
public class CustomerRecommendationListRelatedService extends AbstractGuiService<Customer, Recommendation> {

	@Autowired
	private CustomerRecommendationRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Collection<Booking> bookings = this.repository.findBookingsByCustomerId(customerId);

		Set<String> cities = bookings.stream().map(b -> b.getFlightId() != null ? b.getFlightId().getDestinationCity() : null).filter(s -> s != null && !s.isBlank()).map(String::toLowerCase).collect(Collectors.toCollection(LinkedHashSet::new));

		// si más adelante tienes país destino, añádelo aquí:
		Set<String> countries = new LinkedHashSet<>();

		Collection<Recommendation> data = cities.isEmpty() && countries.isEmpty() ? List.of() : this.repository.findByCitiesOrCountries(cities, countries);

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final Recommendation r) {
		Dataset ds = super.unbindObject(r, "title", "category", "city", "country", "rating", "priceLevel", "url");
		super.getResponse().addData(ds);
	}
}
