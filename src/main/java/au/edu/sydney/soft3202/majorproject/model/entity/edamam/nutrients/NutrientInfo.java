package au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients;

import com.google.gson.annotations.SerializedName;

public class NutrientInfo {

  @SerializedName("unit")
  private String unit;

  @SerializedName("quantity")
  private double quantity;

  @SerializedName("label")
  private String label;

  public String getUnit() {
    return unit;
  }

  public double getQuantity() {
    return quantity;
  }

  public String getLabel() {
    return label;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return "NutrientInfo{"
        + "unit = '"
        + unit
        + '\''
        + ",quantity = '"
        + quantity
        + '\''
        + ",label = '"
        + label
        + '\''
        + "}";
  }
}
