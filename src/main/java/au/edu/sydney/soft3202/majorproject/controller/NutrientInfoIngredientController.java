package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.IngredientParsedItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class NutrientInfoIngredientController implements ModelController<IngredientParsedItem> {
  @FXML protected Label nutrientInfoIngrText;
  @FXML protected GridPane nutrientInfoIngrContainer;
  @FXML protected Label nutrientInfoMeasureText;
  @FXML protected Label nutrientInfoIngrQuantityText;
  @FXML protected Label nutrientInfoIngrRetainedWeightText;
  @FXML protected Label nutrientInfoIngrWeightText;

  @Override
  public void loadDetails(IngredientParsedItem input) {
    this.nutrientInfoIngrText.setText(input.getFood());
    this.nutrientInfoMeasureText.setText(input.getMeasure());
    this.nutrientInfoIngrWeightText.setText(String.format("%.2f", input.getWeight()));
    this.nutrientInfoIngrRetainedWeightText.setText(
        String.format("%.2f", input.getRetainedWeight()));
    this.nutrientInfoIngrQuantityText.setText(String.valueOf(input.getQuantity()));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {}
}
