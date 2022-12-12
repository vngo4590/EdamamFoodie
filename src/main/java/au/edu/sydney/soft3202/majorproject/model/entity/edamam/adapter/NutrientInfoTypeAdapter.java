package au.edu.sydney.soft3202.majorproject.model.entity.edamam.adapter;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientInfo;
import au.edu.sydney.soft3202.majorproject.model.type.Nutrient;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Type adapter for Map<String, NutrientInfo> that tells gson how to read and write pojo classes
 * from and to json
 */
public class NutrientInfoTypeAdapter extends TypeAdapter<Map<String, NutrientInfo>> {

  @Override
  public void write(JsonWriter out, Map<String, NutrientInfo> value) throws IOException {
    Gson gson = new Gson();
    out.beginObject();
    for (Map.Entry<String, NutrientInfo> entry : value.entrySet()) {
      out.name(
          Objects.requireNonNull(Nutrient.getNutrientByTypeName(entry.getKey())).getTypeNTRCode());
      gson.getAdapter(NutrientInfo.class).write(out, entry.getValue());
    }
    out.endObject();
  }

  @Override
  public Map<String, NutrientInfo> read(JsonReader in) throws IOException {
    Map<String, NutrientInfo> nutrientInfoMap = new HashMap<>();
    in.beginObject();
    String fieldname = null;
    Gson gson = new Gson();
    while (in.hasNext()) {
      JsonToken token = in.peek();
      if (token.equals(JsonToken.NAME)) {
        fieldname = in.nextName();
      }
      Nutrient nutrientType = Nutrient.getNutrientByNTR(fieldname);
      if (nutrientType != null && fieldname != null) {
        // move to next token
        token = in.peek();
        if (token.equals(JsonToken.BEGIN_OBJECT)) {
          nutrientInfoMap.put(
              nutrientType.getTypeName(),
              gson.fromJson(in, new TypeToken<NutrientInfo>() {}.getType()));
        }
      }
    }
    in.endObject();
    return nutrientInfoMap;
  }
}
