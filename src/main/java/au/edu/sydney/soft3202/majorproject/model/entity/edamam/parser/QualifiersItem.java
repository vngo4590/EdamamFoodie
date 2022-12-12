package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

public class QualifiersItem {

  @SerializedName("label")
  private String label;

  @SerializedName("uri")
  private String uri;

  public String getLabel() {
    return label;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public String toString() {
    return "QualifiersItem{" + "label = '" + label + '\'' + ",uri = '" + uri + '\'' + "}";
  }
}
