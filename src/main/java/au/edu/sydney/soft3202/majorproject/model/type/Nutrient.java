package au.edu.sydney.soft3202.majorproject.model.type;

/** Select between cooking and food logging processor. */
public enum Nutrient {
  ADDED_SUGAR("Added sugar", "SUGAR.added", UnitMeasurement.GRAM),
  CA("Calcium, Ca", "CA", UnitMeasurement.MILLIGRAM),
  CARBOHYDRATE("Carbohydrate (net)", "CHOCDF.net", UnitMeasurement.GRAM),
  CARBOHYDRATE_DIFF("Carbohydrate, by difference", "CHOCDF", UnitMeasurement.GRAM),
  CHOLE("Cholesterol", "CHOLE", UnitMeasurement.MILLIGRAM),
  ENERGY("Energy", "ENERC_KCAL", UnitMeasurement.KCAL),
  FAMS("Fatty acids, total monounsaturated", "FAMS", UnitMeasurement.GRAM),
  FAPU("Fatty acids, total polyunsaturated", "FAPU", UnitMeasurement.GRAM),
  FASAT("Fatty acids, total saturated", "FASAT", UnitMeasurement.GRAM),
  FATRN("Fatty acids, total trans", "FATRN", UnitMeasurement.GRAM),
  FIBTG("Fiber, total dietary", "FIBTG", UnitMeasurement.GRAM),
  FOLDFE("Folate, DFE", "FOLDFE", UnitMeasurement.MICROGRAM),
  FOLFD("Folate, food", "FOLFD", UnitMeasurement.MICROGRAM),
  FOLAC("Folic acid", "FOLAC", UnitMeasurement.MICROGRAM),
  FE("Iron, Fe", "FE", UnitMeasurement.MILLIGRAM),
  MG("Magnesium", "MG", UnitMeasurement.MILLIGRAM),
  NIA("Niacin", "NIA", UnitMeasurement.MILLIGRAM),
  P("Phosphorus, P", "P", UnitMeasurement.MILLIGRAM),
  K("Potassium, K", "K", UnitMeasurement.MILLIGRAM),
  PROCNT("Protein", "PROCNT", UnitMeasurement.GRAM),
  RIBF("Riboflavin", "RIBF", UnitMeasurement.MILLIGRAM),
  NA("Sodium, Na", "NA", UnitMeasurement.MILLIGRAM),
  SUGAR_ALCOHOL("Sugar alcohols", "Sugar.alcohol", UnitMeasurement.GRAM),
  SUGAR("Sugars, total", "SUGAR", UnitMeasurement.GRAM),
  THIA("Thiamin", "THIA", UnitMeasurement.MILLIGRAM),
  FAT("Total lipid (fat)", "FAT", UnitMeasurement.GRAM),
  VITA_RAE("Vitamin A, RAE", "VITA_RAE", UnitMeasurement.MICROGRAM),
  VITB12("Vitamin B-12", "VITB12", UnitMeasurement.MICROGRAM),
  VITB6A("Vitamin B-6", "VITB6A", UnitMeasurement.MILLIGRAM),
  VITC("Vitamin C, total ascorbic acid", "VITC", UnitMeasurement.MILLIGRAM),
  VITD("Vitamin D (D2 + D3)", "VITD", UnitMeasurement.MICROGRAM),
  TOCPHA("Vitamin E (alpha-tocopherol)", "TOCPHA", UnitMeasurement.MILLIGRAM),
  VITK1("Vitamin K (phylloquinone)", "VITK1", UnitMeasurement.MICROGRAM),
  WATER("Water", "WATER", UnitMeasurement.GRAM),
  ZN("Zinc, Zn", "ZN", UnitMeasurement.MILLIGRAM),
  ;

  private final String typeName;
  private final String typeNTRCode;
  private final UnitMeasurement unitMeasurement;

  Nutrient(String typeName, String typeNTRCode, UnitMeasurement unitMeasurement) {
    this.typeName = typeName;
    this.typeNTRCode = typeNTRCode;
    this.unitMeasurement = unitMeasurement;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getTypeNTRCode() {
    return typeNTRCode;
  }

  public UnitMeasurement getUnitMeasurement() {
    return unitMeasurement;
  }

  public static Nutrient getNutrientByNTR(String typeNTRCode) {
    for (Nutrient n : Nutrient.values()) {
      if (n.getTypeNTRCode().equalsIgnoreCase(typeNTRCode)
          || n.getTypeNTRCode().replace("_", "").replace(".", "").equalsIgnoreCase(typeNTRCode)) {
        return n;
      }
    }
    return null;
  }

  public static Nutrient getNutrientByTypeName(String typeName) {
    for (Nutrient n : Nutrient.values()) {
      if (n.getTypeName().equalsIgnoreCase(typeName)) {
        return n;
      }
    }
    return null;
  }
}
