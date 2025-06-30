
package acme.features.administrator.recommendation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.client.helpers.Assert;
import acme.client.helpers.PrincipalHelper;
import acme.client.helpers.RandomHelper;
import acme.client.helpers.SpringHelper;
import acme.components.recommendation.RecommendationPOJO;
import acme.components.recommendation.ResultsPOJO;
import acme.entities.student2.BusinessStatus;
import acme.entities.student2.Recommendation;

@GuiController
public class AdministratorRecommendationController extends AbstractGuiController<Administrator, Recommendation> {

	// Internal state ---------------------------------------------------------

	private final AdministratorRecommendationRepository		repository;
	private final AdministratorRecommendationListService	listService;
	private final AdministratorRecommendationShowService	showService;

	// Constructors -----------------------------------------------------------


	@Autowired
	public AdministratorRecommendationController(final AdministratorRecommendationRepository repository, final AdministratorRecommendationListService listService, final AdministratorRecommendationShowService showService) {
		this.repository = repository;
		this.listService = listService;
		this.showService = showService;
	}

	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

	// Endpoints --------------------------------------------------------------

	@GetMapping("/administrator/recommendation/populate")
	public ModelAndView populateInitial() {
		Assert.state(PrincipalHelper.get().hasRealmOfType(Administrator.class), "acme.default.error.not-authorised");

		return this.doPopulate();
	}

	// Core population logic --------------------------------------------------

	protected ModelAndView doPopulate() {
		List<String> cities = this.repository.findAllCities();
		Set<String> existingNames = new HashSet<>(this.repository.findAllRecommendationsNames());

		List<List<Recommendation>> recommendations = SpringHelper.isRunningOn("testing") ? cities.stream().map(this::findRecommendationOfCityMocked).toList() : cities.stream().map(this::findRecommendationOfCity).filter(list -> list != null).toList();

		for (List<Recommendation> recList : recommendations)
			for (Recommendation rec : recList) {
				String name = rec.getName();

				if (existingNames.contains(name)) {
					this.repository.delete(this.repository.findRecommendationByName(name));
					existingNames.remove(name); // evita conflictos posteriores
				}

				this.repository.save(rec);
				existingNames.add(name); // marca como ya insertado
			}

		ModelAndView result = new ModelAndView();
		result.setViewName("fragments/welcome");
		result.addObject("_globalSuccessMessage", "acme.default.global.message.success");
		return result;
	}

	// API consumption logic --------------------------------------------------

	protected List<Recommendation> findRecommendationOfCity(final String city) {
		try {
			String formattedCity = city.toLowerCase().replace(" ", "+");
			String apiKey = "AIzaSyC2AU9Q3g-xuKWQiphz4x_meZBn2eKmmTs";
			String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + formattedCity + "+point+of+interest&language=en&key=" + apiKey;

			RestTemplate api = new RestTemplate();
			ResponseEntity<ResultsPOJO> response = api.getForEntity(url, ResultsPOJO.class);

			List<Recommendation> results = new ArrayList<>();
			for (RecommendationPOJO recPOJO : response.getBody().getRecommendations().subList(0, 5)) {
				Recommendation recommendation = Recommendation.of(recPOJO, city);
				if (recommendation != null)
					results.add(recommendation);
			}

			return results;
		} catch (final Throwable oops) {
			return null;
		}
	}

	protected List<Recommendation> findRecommendationOfCityMocked(final String city) {
		Recommendation rec = new Recommendation();
		rec.setCity(city);
		rec.setBusinessStatus(BusinessStatus.values()[RandomHelper.nextInt(0, 3)]);
		rec.setFormattedAddress("Sample address");
		rec.setName("Recommendation in " + city);
		rec.setOpenNow(RandomHelper.nextInt(0, 2) == 0);
		rec.setPhotoReference("https://st2.depositphotos.com/3047529/9390/i/450/depositphotos_93900498-stock-photo-mcdonalds-logo-on-a-pole.jpg");
		rec.setRating(RandomHelper.nextDouble(0., 5.));
		rec.setUserRatingsTotal(RandomHelper.nextInt(0, 999999));

		List<Recommendation> result = new ArrayList<>();
		result.add(rec);
		return result;
	}
}
