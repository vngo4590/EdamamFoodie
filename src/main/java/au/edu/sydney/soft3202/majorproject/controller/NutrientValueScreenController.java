package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class NutrientValueScreenController implements ModelController<NutrientInfo> {
  @FXML protected GridPane nutrientValueScreen;
  @FXML protected Label nutrientLabelText;
  @FXML protected Label nutrientMeasureText;
  @FXML protected Label nutrientQuantityText;

  @Override
  public void loadDetails(NutrientInfo input) {
    nutrientLabelText.setText(input.getLabel());
    nutrientMeasureText.setText(input.getUnit());
    nutrientQuantityText.setText(String.format("%.2f", input.getQuantity()));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {}
}
