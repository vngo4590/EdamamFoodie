package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestBody;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestData;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.input.IngredientRequestWrapper;
import au.edu.sydney.soft3202.majorproject.model.type.MeasureType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class IngredientRequestDisplayController implements ModelController<IngredientRequestWrapper> {
  @FXML protected GridPane ingredientRequestDisplayScreen;
  @FXML protected VBox foodMeasureDisplayContainer;
  @FXML protected Label foodLabel;

  private static final Logger LOGGER =
      LogManager.getLogger(IngredientRequestDisplayController.class);

  @Override
  public void loadDetails(IngredientRequestWrapper input) {
    foodLabel.setText(input.getFoodLabel());
    loadIngredientMeasure(input);
  }

  private void loadIngredientMeasure(IngredientRequestWrapper requestWrapper) {
    IngredientRequestBody requestBody = requestWrapper.getRequestBody();
    if (requestBody != null && requestBody.getIngredients() != null) {
      for (IngredientRequestData data : requestBody.getIngredients()) {
        LOGGER.info("Showing ingredient request data: " + data);
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.wrapTextProperty().set(true);
        Label measureLabel = new Label("Measure:");
        measureLabel.wrapTextProperty().set(true);
        Label quantityValue = new Label(String.valueOf(data.getQuantity()));
        quantityValue.wrapTextProperty().set(true);
        Label measureValue =
            new Label(
                Objects.requireNonNull(MeasureType.getMeasureTypeByURI(data.getMeasureURI()))
                    .getTypeName());
        measureValue.wrapTextProperty();
        GridPane gridPane = new GridPane();
        gridPane.add(quantityLabel, 0, 1);
        gridPane.add(quantityValue, 1, 1);
        gridPane.add(measureLabel, 0, 0);
        gridPane.add(measureValue, 1, 0);
        gridPane.setStyle("-fx-border-style: solid;");
        this.foodMeasureDisplayContainer.getChildren().add(gridPane);
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {}
}
