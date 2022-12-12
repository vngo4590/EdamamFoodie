package au.edu.sydney.soft3202.majorproject.controller;

import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenLoaderUtils;
import au.edu.sydney.soft3202.majorproject.controller.utils.ScreenType;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientInfo;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.presenter.NutrientStackedBarTabsScreenPresenter;
import au.edu.sydney.soft3202.majorproject.presenter.utils.PresenterUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class NutrientStackedBarTabsScreenController implements ModelController<NutrientsResponse> {
  @FXML public VBox nutrientStackedBarTabsScreen;
  @FXML public TabPane nutrientStackedBarTabsContainer;
  private static final int MAX_NUTRIENT_PER_TAB = 4;
  private NutrientStackedBarTabsScreenPresenter nutrientStackedBarTabsScreenPresenter;
  private static final Logger LOGGER =
      LogManager.getLogger(NutrientStackedBarTabsScreenController.class);

  @Override
  public void loadDetails(NutrientsResponse input) {
    LOGGER.info("Setting up stacked bar chart");
    /*
     * Load tabs by nutrient limit
     * */
    int index = 0;
    int tabIndex = 0;
    int sizeIndex = 0;

    Map<String, NutrientInfo> nutrientInfoMap = input.getTotalNutrients();
    Map<String, Double> nutrientMap = new HashMap<>();
    Tab tab = new Tab();
    for (Map.Entry<String, NutrientInfo> entry : nutrientInfoMap.entrySet()) {
      if ((index % MAX_NUTRIENT_PER_TAB == 0 && nutrientMap.size() == MAX_NUTRIENT_PER_TAB)
          || sizeIndex == 0) {
        nutrientMap = new HashMap<>();
        tab = new Tab();
        tab.setText("Graph " + (tabIndex + 1));
        tab.setClosable(false);
        this.nutrientStackedBarTabsContainer.getTabs().add(tab);
        tabIndex++;
      }
      double maxNutrient =
          nutrientStackedBarTabsScreenPresenter.getNutrientMaxValue(entry.getKey());
      if (maxNutrient > 0) {
        nutrientMap.put(
            entry.getKey(),
            nutrientStackedBarTabsScreenPresenter.getNutrientPercentage(
                input.getTotalNutrients().get(entry.getKey()).getQuantity(), maxNutrient));
        index++;
      }
      sizeIndex++;
      if (index % MAX_NUTRIENT_PER_TAB == 0 || sizeIndex == nutrientInfoMap.size()) {
        try {
          FXMLLoader loader =
              ScreenLoaderUtils.loadModelViewController(
                  ScreenType.NUTRIENT_STACKED_BAR_CHART, nutrientMap);
          tab.setContent(loader.getRoot());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (nutrientMap.size() == 0 && nutrientStackedBarTabsContainer.getTabs().size() > 1) {
      nutrientStackedBarTabsContainer.getTabs().remove(tab);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nutrientStackedBarTabsScreenPresenter = new NutrientStackedBarTabsScreenPresenter(this);
  }
}
