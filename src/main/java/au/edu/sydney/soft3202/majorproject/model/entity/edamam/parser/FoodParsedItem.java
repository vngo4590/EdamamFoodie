package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

public class FoodParsedItem {

  @SerializedName("food")
  private FoodInfo foodInfo;

  public FoodInfo getFood() {
    return foodInfo;
  }

  @Override
  public String toString() {
    return "ParsedItem{" + "food = '" + foodInfo + '\'' + "}";
  }
}
