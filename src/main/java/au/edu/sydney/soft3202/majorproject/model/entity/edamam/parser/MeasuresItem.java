package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeasuresItem {

  @SerializedName("weight")
  private double weight;

  @SerializedName("label")
  private String label;

  @SerializedName("uri")
  private String uri;

  @SerializedName("qualified")
  private List<QualifiedItem> qualified;

  public double getWeight() {
    return weight;
  }

  public String getLabel() {
    return label;
  }

  public String getUri() {
    return uri;
  }

  public List<QualifiedItem> getQualified() {
    return qualified;
  }

  @Override
  public String toString() {
    return "MeasuresItem{"
        + "weight = '"
        + weight
        + '\''
        + ",label = '"
        + label
        + '\''
        + ",uri = '"
        + uri
        + '\''
        + ",qualified = '"
        + qualified
        + '\''
        + "}";
  }
}
