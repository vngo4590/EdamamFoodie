package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class FoodHintsItem {

  @SerializedName("measures")
  private List<MeasuresItem> measures;

  @SerializedName("food")
  private FoodInfo foodInfo;

  public List<MeasuresItem> getMeasures() {
    return measures;
  }

  public FoodInfo getFood() {
    return foodInfo;
  }

  @Override
  public String toString() {
    return "HintsItem{" + "measures = '" + measures + '\'' + ",food = '" + foodInfo + '\'' + "}";
  }
}
