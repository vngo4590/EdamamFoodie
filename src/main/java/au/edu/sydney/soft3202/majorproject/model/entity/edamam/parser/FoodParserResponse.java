package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class FoodParserResponse {

  @SerializedName("_links")
  private Links links;

  @SerializedName("hints")
  private List<FoodHintsItem> hints;

  @SerializedName("parsed")
  private List<FoodParsedItem> parsed;

  @SerializedName("text")
  private String text;

  public Links getLinks() {
    return links;
  }

  public List<FoodHintsItem> getHints() {
    return hints;
  }

  public List<FoodParsedItem> getParsed() {
    return parsed;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "FoodParserResponse{"
        + "_links = '"
        + links
        + '\''
        + ",hints = '"
        + hints
        + '\''
        + ",parsed = '"
        + parsed
        + '\''
        + ",text = '"
        + text
        + '\''
        + "}";
  }
}
