package au.edu.sydney.soft3202.majorproject.model.entity.edamam.input;

import com.google.gson.annotations.SerializedName;

public class IngredientRequestData {

  @SerializedName("quantity")
  private int quantity;

  @SerializedName("foodId")
  private String foodId;

  @SerializedName("measureURI")
  private String measureURI;

  public int getQuantity() {
    return quantity;
  }

  public String getFoodId() {
    return foodId;
  }

  public String getMeasureURI() {
    return measureURI;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setFoodId(String foodId) {
    this.foodId = foodId;
  }

  public void setMeasureURI(String measureURI) {
    this.measureURI = measureURI;
  }

  @Override
  public String toString() {
    return "IngredientRequestData{"
        + "quantity="
        + quantity
        + ", foodId='"
        + foodId
        + '\''
        + ", measureURI='"
        + measureURI
        + '\''
        + '}';
  }
}
