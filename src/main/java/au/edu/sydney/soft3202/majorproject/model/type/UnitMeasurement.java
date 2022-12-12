package au.edu.sydney.soft3202.majorproject.model.type;

public enum UnitMeasurement {
  GRAM("g"),
  MILLIGRAM("mg"),
  KCAL("kcal"),
  MICROGRAM("µg");

  private final String typeName;

  UnitMeasurement(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }

  public static UnitMeasurement getUnitMeasurementByType(String typeName) {
    for (UnitMeasurement unitMeasurement : UnitMeasurement.values()) {
      if (unitMeasurement.typeName.equalsIgnoreCase(typeName)) {
        return unitMeasurement;
      }
    }
    return null;
  }
}
