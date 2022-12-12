package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

public class ServingSizesItem {

  @SerializedName("quantity")
  private double quantity;

  @SerializedName("label")
  private String label;

  @SerializedName("uri")
  private String uri;

  public double getQuantity() {
    return quantity;
  }

  public String getLabel() {
    return label;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public String toString() {
    return "ServingSizesItem{"
        + "quantity = '"
        + quantity
        + '\''
        + ",label = '"
        + label
        + '\''
        + ",uri = '"
        + uri
        + '\''
        + "}";
  }
}
