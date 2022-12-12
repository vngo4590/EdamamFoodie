package au.edu.sydney.soft3202.majorproject.presenter;

import au.edu.sydney.soft3202.majorproject.controller.NutrientStackedBarTabsScreenController;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.EdamamRepositorySingleton;
import au.edu.sydney.soft3202.majorproject.presenter.utils.PresenterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NutrientStackedBarTabsScreenPresenter {
  private static final Logger LOGGER =
      LogManager.getLogger(NutrientStackedBarTabsScreenPresenter.class);
  private NutrientStackedBarTabsScreenController nutrientStackedBarTabsScreenController;
  private final EdamamRepository edamamRepository;

  public NutrientStackedBarTabsScreenPresenter(
      NutrientStackedBarTabsScreenController nutrientStackedBarTabsScreenController) {
    this.nutrientStackedBarTabsScreenController = nutrientStackedBarTabsScreenController;
    this.edamamRepository = EdamamRepositorySingleton.getInstance();
  }

  /**
   * Get the nutrient max value of a given nutrient
   *
   * @param nutrient the nutrient name
   */
  public double getNutrientMaxValue(String nutrient) {
    return edamamRepository.getQueryBuilder().getMax(nutrient);
  }

  /**
   * Get the nutrient value
   *
   * @param nutrientValue the nutrient value
   * @param maxNutrientValue the upper bound of the nutrient value
   */
  public double getNutrientPercentage(double nutrientValue, double maxNutrientValue) {
    return PresenterUtils.getNutrientPercentage(nutrientValue, maxNutrientValue);
  }
}
