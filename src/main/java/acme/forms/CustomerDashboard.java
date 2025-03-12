
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.S2.TravelClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	List<String>				lastFiveDestinations;
	Money						moneyInBookings;
	Map<TravelClass, Integer>	bookingsByTravelClass;
	Money						countBookingCost;
	Money						averageBookingCost;
	Money						minimumBookingCost;
	Money						maximumBookingCost;
	Money						standardDeviationBookingCost;
	int							countNumberPassengers;
	Double						averageNumberPassengers;
	Integer						minimumNumberPassengers;
	Integer						maximumNumberPassengers;
	Double						standardDeviationNumberPassengers;

}
