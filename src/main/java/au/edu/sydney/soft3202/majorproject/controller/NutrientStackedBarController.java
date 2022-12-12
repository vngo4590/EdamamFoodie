package au.edu.sydney.soft3202.majorproject.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class NutrientStackedBarController implements ModelController<Map<String, Double>> {
  @FXML protected VBox nutrientStackedBarContainer;
  @FXML protected StackedBarChart<String, Double> nutrientStackedBar;

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  @Override
  public void loadDetails(Map<String, Double> input) {
    /*
     * For all nutrient, set value to maximum
     * */
    final XYChart.Series<String, Double> nutrientSeries = new XYChart.Series<>();
    final XYChart.Series<String, Double> maxSeries = new XYChart.Series<>();
    nutrientSeries.setName("Nutrient Info Percentage %");
    maxSeries.setName("Nutrient value 100%");
    for (Map.Entry<String, Double> entry : input.entrySet()) {
      nutrientSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue() * 100));
      maxSeries.getData().add(new XYChart.Data<>(entry.getKey(), 100 - entry.getValue() * 100));
    }
    nutrientStackedBar.getData().add(nutrientSeries);
    nutrientStackedBar.getData().add(maxSeries);
  }
}
