package au.edu.sydney.soft3202.majorproject.model.entity.edamam.input;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class IngredientRequestBody {

  @SerializedName("ingredients")
  private List<IngredientRequestData> ingredients;

  public List<IngredientRequestData> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<IngredientRequestData> ingredients) {
    this.ingredients = ingredients;
  }

  @Override
  public String toString() {
    return "IngredientRequestBody{" + "ingredients=" + ingredients + '}';
  }
}
