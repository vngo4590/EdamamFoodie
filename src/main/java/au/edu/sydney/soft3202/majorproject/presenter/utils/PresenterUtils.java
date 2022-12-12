package au.edu.sydney.soft3202.majorproject.presenter.utils;

public class PresenterUtils {
    public static double getNutrientPercentage(double nutrientValue, double maxNutrientValue) {
      // Max nutrient is the nutrient per totalWeight unit
      double result = (nutrientValue) / (maxNutrientValue);
      if (result > 1) {
          result = maxNutrientValue / nutrientValue;
      }
      return result;
    }
}
