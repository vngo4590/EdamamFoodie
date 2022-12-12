package au.edu.sydney.soft3202.majorproject.model.entity.edamam.input;

public class IngredientRequestWrapper {
  private IngredientRequestBody requestBody;

  private String foodLabel;

  public IngredientRequestBody getRequestBody() {
    return requestBody;
  }

  public void setRequestBody(IngredientRequestBody requestBody) {
    this.requestBody = requestBody;
  }

  public String getFoodLabel() {
    return foodLabel;
  }

  public void setFoodLabel(String foodLabel) {
    this.foodLabel = foodLabel;
  }

  @Override
  public String toString() {
    return "IngredientRequestWrapper{"
        + "requestBody="
        + requestBody
        + ", foodLabel='"
        + foodLabel
        + '\''
        + '}';
  }
}
