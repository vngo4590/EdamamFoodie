package au.edu.sydney.soft3202.majorproject.model.type;

public enum MeasureType {
  OUNCE("Ounce", "http://www.edamam.com/ontologies/edamam.owl#Measure_ounce"),
  GRAM("Gram", "http://www.edamam.com/ontologies/edamam.owl#Measure_gram"),
  POUND("Pound", "http://www.edamam.com/ontologies/edamam.owl#Measure_pound"),
  KILOGRAM("Kilogram", "http://www.edamam.com/ontologies/edamam.owl#Measure_kilogram"),
  PINCH("Pinch", "http://www.edamam.com/ontologies/edamam.owl#Measure_pinch"),
  LITER("Liter", "http://www.edamam.com/ontologies/edamam.owl#Measure_liter"),
  FLUID_OUNCE("Fluid ounce", "http://www.edamam.com/ontologies/edamam.owl#Measure_fluid_ounce"),
  GALLON("Gallon", "http://www.edamam.com/ontologies/edamam.owl#Measure_gallon"),
  PINT("Pint", "http://www.edamam.com/ontologies/edamam.owl#Measure_pint"),
  QUART("Quart", "http://www.edamam.com/ontologies/edamam.owl#Measure_quart"),
  MILLILITER("Milliliter", "http://www.edamam.com/ontologies/edamam.owl#Measure_milliliter"),
  DROP("Drop", "http://www.edamam.com/ontologies/edamam.owl#Measure_drop"),
  CUP("Cup", "http://www.edamam.com/ontologies/edamam.owl#Measure_cup"),
  TABLESPOON("Tablespoon", "http://www.edamam.com/ontologies/edamam.owl#Measure_tablespoon"),
  TEASPOON("Teaspoon", "http://www.edamam.com/ontologies/edamam.owl#Measure_teaspoon");

  private final String typeName;
  private final String uri;

  MeasureType(String typeName, String uri) {
    this.typeName = typeName;
    this.uri = uri;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getUri() {
    return uri;
  }

  public static MeasureType getMeasureTypeByURI(String uri) {
    for (MeasureType m : MeasureType.values()) {
      if (m.getUri().equalsIgnoreCase(uri)) {
        return m;
      }
    }
    return null;
  }

  public static MeasureType getMeasureTypeByName(String name) {
    for (MeasureType m : MeasureType.values()) {
      if (m.getTypeName().equalsIgnoreCase(name)) {
        return m;
      }
    }
    return null;
  }
}
