package au.edu.sydney.soft3202.majorproject.model.type;
/** Select between cooking and food logging processor. */
public enum NutritionalType {
  COOKING("cooking"),
  LOGGING("logging");

  private final String typeName;

  NutritionalType(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }
}
