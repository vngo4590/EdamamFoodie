package au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients;

import java.util.List;
import java.util.Map;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.adapter.IngredientParsedItemTypeAdapter;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.adapter.NutrientInfoTypeAdapter;
import au.edu.sydney.soft3202.majorproject.model.general.FlattenSerializable;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

public class NutrientsResponse implements FlattenSerializable {

  @SerializedName("healthLabels")
  private List<String> healthLabels;

  @SerializedName("dietLabels")
  private List<String> dietLabels;

  @SerializedName("cautions")
  private List<String> cautions;

  @SerializedName("totalWeight")
  private double totalWeight;

  @SerializedName("calories")
  private double calories;

  @SerializedName("uri")
  private String uri;

  /**
   * The ingredient that this nutrient info is for
   *
   * <p>Note that we always treat this list as of 1 unit size
   */
  @SerializedName("ingredients")
  @JsonAdapter(IngredientParsedItemTypeAdapter.class)
  private List<IngredientParsedItem> ingredients;

  @SerializedName("totalDaily")
  @JsonAdapter(NutrientInfoTypeAdapter.class)
  private Map<String, NutrientInfo> totalDaily;

  @SerializedName("totalNutrients")
  @JsonAdapter(NutrientInfoTypeAdapter.class)
  private Map<String, NutrientInfo> totalNutrients;

  public List<String> getHealthLabels() {
    return healthLabels;
  }

  public double getTotalWeight() {
    return totalWeight;
  }

  public List<IngredientParsedItem> getIngredients() {
    return ingredients;
  }

  public Map<String, NutrientInfo> getTotalDaily() {
    return totalDaily;
  }

  public double getCalories() {
    return calories;
  }

  public String getUri() {
    return uri;
  }

  public Map<String, NutrientInfo> getTotalNutrients() {
    return totalNutrients;
  }

  public List<String> getCautions() {
    return cautions;
  }

  public List<String> getDietLabels() {
    return dietLabels;
  }

  public void setHealthLabels(List<String> healthLabels) {
    this.healthLabels = healthLabels;
  }

  public void setDietLabels(List<String> dietLabels) {
    this.dietLabels = dietLabels;
  }

  public void setCautions(List<String> cautions) {
    this.cautions = cautions;
  }

  public void setTotalWeight(double totalWeight) {
    this.totalWeight = totalWeight;
  }

  public void setCalories(double calories) {
    this.calories = calories;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setIngredients(List<IngredientParsedItem> ingredients) {
    this.ingredients = ingredients;
  }

  public void setTotalDaily(Map<String, NutrientInfo> totalDaily) {
    this.totalDaily = totalDaily;
  }

  public void setTotalNutrients(Map<String, NutrientInfo> totalNutrients) {
    this.totalNutrients = totalNutrients;
  }

  @Override
  public String toString() {
    return "NutrientsResponse{"
        + "healthLabels = '"
        + healthLabels
        + '\''
        + ",dietLabels = '"
        + dietLabels
        + '\''
        + ",totalWeight = '"
        + totalWeight
        + '\''
        + ",cautions = '"
        + cautions
        + '\''
        + ",ingredients = '"
        + ingredients
        + '\''
        + ",totalDaily = '"
        + totalDaily
        + '\''
        + ",calories = '"
        + calories
        + '\''
        + ",uri = '"
        + uri
        + '\''
        + ",totalNutrients = '"
        + totalNutrients
        + '\''
        + "}";
  }

  @Override
  public String flatten() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.getIngredients() != null && this.getIngredients().get(0) != null) {
      for (IngredientParsedItem ingredientParsedItem : this.getIngredients()) {
        for (Map.Entry<String, NutrientInfo> nutrientInfoEntry :
            this.getTotalNutrients().entrySet()) {
          double totalNutrient =
              (nutrientInfoEntry.getValue().getQuantity() / this.totalWeight)
                  * ingredientParsedItem.getWeight();
          stringBuilder
              .append(
                  String.format(
                      "(%s, %s, %s)",
                      ingredientParsedItem.getFood(),
                      ingredientParsedItem.getMeasure(),
                      ingredientParsedItem.getQuantity()))
              .append(" ")
              .append(String.format("%s : %f", nutrientInfoEntry.getKey(), totalNutrient))
              .append("\n");
        }
      }
    }
    return stringBuilder.toString();
  }
}
