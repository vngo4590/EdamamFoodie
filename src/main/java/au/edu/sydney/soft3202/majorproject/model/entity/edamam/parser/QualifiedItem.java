package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class QualifiedItem {

  @SerializedName("qualifiers")
  private List<QualifiersItem> qualifiers;

  @SerializedName("weight")
  private double weight;

  public List<QualifiersItem> getQualifiers() {
    return qualifiers;
  }

  public double getWeight() {
    return weight;
  }

  @Override
  public String toString() {
    return "QualifiedItem{"
        + "qualifiers = '"
        + qualifiers
        + '\''
        + ",weight = '"
        + weight
        + '\''
        + "}";
  }
}
