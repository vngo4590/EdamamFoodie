package au.edu.sydney.soft3202.majorproject.model.db;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;

import java.sql.SQLException;
import java.util.List;

public interface NutrientDao {
  /**
   * insert or overwrite nutrient response to the cache. <br>
   *
   * @throws SQLException If there exists the same data while doOverwrite=false
   */
  void insertNutrientResponse(NutrientsResponse nutrientsResponse) throws SQLException;
  /** Get the nutrient response from the cache */
  String getNutrientResponse(String foodId, String measureUri, int quantity) throws SQLException;
  /** Delete and refresh all responses */
  void deleteAllNutrientResponses();
}
