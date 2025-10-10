
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// 1) Últimos 5 destinos (ordenados por compra desc)
	private List<String>		lastFiveDestinations;

	// 2) Dinero gastado en el último año
	private Double				moneySpentLastYear; // en la divisa base (importe)

	// 3) Nº de bookings agrupadas por travel class
	private Map<String, Long>	bookingsByTravelClass;

	// 4) Coste de sus bookings en los últimos 5 años: count, avg, min, max, stddev
	private Long				costCount5y;
	private Double				costAvg5y;
	private Double				costMin5y;
	private Double				costMax5y;
	private Double				costStddev5y;

	// 5) Nº de pasajeros por booking: count, avg, min, max, stddev
	private Long				paxCountSamples;   // nº de muestras (bookings)
	private Double				paxAvg;
	private Long				paxMin;
	private Long				paxMax;
	private Double				paxStddev;
}
