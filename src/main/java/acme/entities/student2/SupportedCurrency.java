
package acme.entities.student2;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidCurrency;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.components.exchange.AllowedExchangePOJO;
import acme.components.exchange.ExchangePOJO;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "supported_currency", indexes = {
	@Index(name = "idx_supported_currency_name", columnList = "currencyName", unique = true), @Index(name = "idx_supported_currency_default", columnList = "isDefaultCurrency")
})
public class SupportedCurrency extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long						serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidCurrency
	@Column(unique = true)
	private String									currencyName;

	@Mandatory
	@Automapped
	private Boolean									isDefaultCurrency;

	// Derived attributes -----------------------------------------------------

	@Transient
	private static Map<String, Map<String, Double>>	lastData;


	@Transient
	public static String getDefaultCurrency() {
		SupportedCurrencyRepository repository = SpringHelper.getBean(SupportedCurrencyRepository.class);
		return repository.getDefaultCurrency();
	}

	@Transient
	public static Set<String> getAllowedCurrencies() {
		Set<String> res = new HashSet<>();

		if (SpringHelper.isRunningOn("production"))
			try {
				String apiKey = "fca_live_LoNLzqN8xfOE524QdmeycQrAkUMvlwcsWGd5nEhw";
				String url = "https://api.freecurrencyapi.com/v1/currencies?apikey=" + apiKey;
				RestTemplate api = new RestTemplate();
				ResponseEntity<AllowedExchangePOJO> response = api.getForEntity(url, AllowedExchangePOJO.class);
				Map<String, Map<String, String>> data = response.getBody().getData();
				res = data.keySet();
			} catch (final Throwable oops) {
				System.out.println(oops);
			}
		else
			res = Set.of("EUR", "USD", "JPY", "BGN", "CZK", "DKK", "GBP", "HUF", "PLN", "RON", "SEK", "CHF", "ISK", "RUB", "TRY", "AUD", "PHP", "ZAR");

		return res;
	}

	@Transient
	public static Money convertToDefault(final Money money) {
		Money result = new Money();
		String defaultCurrency = SupportedCurrency.getDefaultCurrency();
		result.setCurrency(defaultCurrency);

		if (money.getCurrency().equals(defaultCurrency))
			return money;

		if (SpringHelper.isRunningOn("production"))
			return SupportedCurrency.convertUsingApi(money, defaultCurrency);
		else
			return SupportedCurrency.convertMocked(money, defaultCurrency);
	}

	private static Money convertUsingApi(final Money money, final String defaultCurrency) {
		Money result = new Money();
		result.setCurrency(defaultCurrency);

		try {
			String nowFormatted = SupportedCurrency.getFormattedDate();
			Map<String, Map<String, Double>> data = SupportedCurrency.getExchangeData(nowFormatted, defaultCurrency);
			Double rate = data.values().iterator().next().get(money.getCurrency());
			result.setAmount(money.getAmount() / rate);
		} catch (final Throwable oops) {
			System.out.println(oops);
			result.setAmount(0.0);
		}
		return result;
	}

	private static Map<String, Map<String, Double>> getExchangeData(final String date, final String defaultCurrency) {
		if (SupportedCurrency.lastData != null && SupportedCurrency.lastData.get(date) != null)
			return SupportedCurrency.lastData;
		String apiKey = "fca_live_LoNLzqN8xfOE524QdmeycQrAkUMvlwcsWGd5nEhw";
		String url = "https://api.freecurrencyapi.com/v1/historical?apikey=" + apiKey + "&date=" + date + "&base_currency=" + defaultCurrency;

		RestTemplate api = new RestTemplate();
		ResponseEntity<ExchangePOJO> response = api.getForEntity(url, ExchangePOJO.class);
		Map<String, Map<String, Double>> data = response.getBody().getData();
		SupportedCurrency.lastData = data;
		return data;
	}

	private static String getFormattedDate() {
		Date now = MomentHelper.getCurrentMoment();
		@SuppressWarnings("deprecation")
		String formatted = now.getYear() + 1900 + "-" + String.format("%02d", now.getMonth() + 1) + "-" + String.format("%02d", now.getDate());
		return formatted;
	}

	private static Money convertMocked(final Money money, final String defaultCurrency) {
		Money result = new Money();
		result.setCurrency(defaultCurrency);

		Map<String, Double> mockedValues = new HashMap<>();
		mockedValues.put("GBP", 0.8270029409);
		mockedValues.put("USD", 1.0352287186);

		if ("EUR".equals(defaultCurrency) && mockedValues.containsKey(money.getCurrency())) {
			Double rate = mockedValues.get(money.getCurrency());
			result.setAmount(money.getAmount() / rate);
		} else
			result.setAmount(0.0);
		return result;
	}
}
