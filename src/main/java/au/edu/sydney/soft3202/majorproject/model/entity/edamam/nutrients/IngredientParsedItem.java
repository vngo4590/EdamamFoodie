package au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients;

import com.google.gson.annotations.SerializedName;

public class IngredientParsedItem {

  @SerializedName("quantity")
  private int quantity;

  @SerializedName("measure")
  private String measure;

  @SerializedName("retainedWeight")
  private double retainedWeight;

  @SerializedName("foodId")
  private String foodId;

  @SerializedName("weight")
  private double weight;

  @SerializedName("food")
  private String food;

  @SerializedName("measureURI")
  private String measureURI;

  @SerializedName("status")
  private String status;

  @SerializedName("foodContentsLabel")
  private String foodContentsLabel;

  public int getQuantity() {
    return quantity;
  }

  public String getMeasure() {
    return measure;
  }

  public double getRetainedWeight() {
    return retainedWeight;
  }

  public String getFoodId() {
    return foodId;
  }

  public double getWeight() {
    return weight;
  }

  public String getFood() {
    return food;
  }

  public String getMeasureURI() {
    return measureURI;
  }

  public String getStatus() {
    return status;
  }

  public String getFoodContentsLabel() {
    return foodContentsLabel;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setMeasure(String measure) {
    this.measure = measure;
  }

  public void setRetainedWeight(double retainedWeight) {
    this.retainedWeight = retainedWeight;
  }

  public void setFoodId(String foodId) {
    this.foodId = foodId;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public void setFood(String food) {
    this.food = food;
  }

  public void setMeasureURI(String measureURI) {
    this.measureURI = measureURI;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setFoodContentsLabel(String foodContentsLabel) {
    this.foodContentsLabel = foodContentsLabel;
  }

  @Override
  public String toString() {
    return "IngredientParsedItem{"
        + "quantity = '"
        + quantity
        + '\''
        + ",measure = '"
        + measure
        + '\''
        + ",retainedWeight = '"
        + retainedWeight
        + '\''
        + ",foodId = '"
        + foodId
        + '\''
        + ",foodContentsLabel = '"
        + foodContentsLabel
        + '\''
        + ",weight = '"
        + weight
        + '\''
        + ",food = '"
        + food
        + '\''
        + ",measureURI = '"
        + measureURI
        + '\''
        + ",status = '"
        + status
        + '\''
        + "}";
  }
}
