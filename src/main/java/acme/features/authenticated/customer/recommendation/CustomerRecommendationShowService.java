
package acme.features.authenticated.customer.recommendation;

import java.util.Collection;
import java.util.LinkedHashSet;
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
public class CustomerRecommendationShowService extends AbstractGuiService<Customer, Recommendation> {

	@Autowired
	private CustomerRecommendationRepository repository;


	@Override
	public void authorise() {
		final boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		boolean authorised = false;

		if (isCustomer) {
			final int id = super.getRequest().getData("id", int.class);
			final Recommendation rec = this.repository.findOneById(id);

			if (rec != null) {
				final int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				final Collection<Booking> bookings = this.repository.findBookingsByCustomerId(customerId);

				final Set<String> cities = bookings.stream().map(b -> b.getFlightId() != null ? b.getFlightId().getDestinationCity() : null).filter(s -> s != null && !s.isBlank()).map(String::toLowerCase)
					.collect(Collectors.toCollection(LinkedHashSet::new));

				// si no manejas país aún, lo dejamos vacío
				final Set<String> countries = Set.of();

				final String recCity = rec.getCity() != null ? rec.getCity().toLowerCase() : null;
				final String recCountry = rec.getCountry() != null ? rec.getCountry().toLowerCase() : null;

				authorised = recCity != null && cities.contains(recCity) || recCountry != null && countries.contains(recCountry);
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		final int id = super.getRequest().getData("id", int.class);
		final Recommendation rec = this.repository.findOneById(id);
		super.getBuffer().addData(rec);
	}

	@Override
	public void unbind(final Recommendation rec) {
		final Dataset ds = super.unbindObject(rec, "title", "category", "city", "country", "shortDescription", "url", "imageUrl", "rating", "priceLevel", "lastUpdate", "source", "externalId");

		super.getResponse().addData(ds);
	}
}
