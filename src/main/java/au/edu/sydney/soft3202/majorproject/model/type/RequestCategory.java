package au.edu.sydney.soft3202.majorproject.model.type;
/**
 * Category is a parameter that can be used when making a request in order to narrow down the
 * results.
 */
public enum RequestCategory {
  GENERIC_FOODS("Generic Food", "generic-foods"),
  PACKAGED_FOODS("Packaged Foods", "packaged-foods"),
  GENERIC_MEALS("Generic Meals", "generic-meals"),
  FAST_FOODS("Fast Foods", "fast-foods");

  private final String typeName;
  private final String typeCode;

  RequestCategory(String typeName, String typeCode) {
    this.typeName = typeName;
    this.typeCode = typeCode;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getTypeCode() {
    return typeCode;
  }
}
