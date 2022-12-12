package au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodInfo {

  @SerializedName("foodContentsLabel")
  private String foodContentsLabel;

  @SerializedName("foodId")
  private String foodId;

  @SerializedName("categoryLabel")
  private String categoryLabel;

  @SerializedName("label")
  private String label;

  @SerializedName("category")
  private String category;

  @SerializedName("nutrients")
  private Nutrients nutrients;

  @SerializedName("image")
  private String image;

  @SerializedName("servingSizes")
  private List<ServingSizesItem> servingSizes;

  @SerializedName("brand")
  private String brand;

  @SerializedName("servingsPerContainer")
  private double servingsPerContainer;

  public String getFoodContentsLabel() {
    return foodContentsLabel;
  }

  public String getFoodId() {
    return foodId;
  }

  public String getCategoryLabel() {
    return categoryLabel;
  }

  public String getLabel() {
    return label;
  }

  public String getCategory() {
    return category;
  }

  public Nutrients getNutrients() {
    return nutrients;
  }

  public String getImage() {
    return image;
  }

  public List<ServingSizesItem> getServingSizes() {
    return servingSizes;
  }

  public String getBrand() {
    return brand;
  }

  public double getServingsPerContainer() {
    return servingsPerContainer;
  }

  @Override
  public String toString() {
    return "Food{"
        + "foodContentsLabel = '"
        + foodContentsLabel
        + '\''
        + ",foodId = '"
        + foodId
        + '\''
        + ",categoryLabel = '"
        + categoryLabel
        + '\''
        + ",label = '"
        + label
        + '\''
        + ",category = '"
        + category
        + '\''
        + ",nutrients = '"
        + nutrients
        + '\''
        + ",image = '"
        + image
        + '\''
        + ",servingSizes = '"
        + servingSizes
        + '\''
        + ",brand = '"
        + brand
        + '\''
        + ",servingsPerContainer = '"
        + servingsPerContainer
        + '\''
        + "}";
  }
}
