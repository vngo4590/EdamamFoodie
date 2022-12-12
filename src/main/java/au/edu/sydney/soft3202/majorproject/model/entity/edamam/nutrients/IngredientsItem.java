package au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class IngredientsItem {

  @SerializedName("parsed")
  private List<IngredientParsedItem> parsed;

  public List<IngredientParsedItem> getParsed() {
    return parsed;
  }

  @Override
  public String toString() {
    return "IngredientsItem{" + "parsed = '" + parsed + '\'' + "}";
  }
}
