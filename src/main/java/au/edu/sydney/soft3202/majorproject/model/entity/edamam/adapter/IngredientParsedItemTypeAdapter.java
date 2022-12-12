package au.edu.sydney.soft3202.majorproject.model.entity.edamam.adapter;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.IngredientParsedItem;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.IngredientsItem;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Type adapter for List<IngredientParsedItem> that tells gson how to read and write pojo classes
 * from and to json
 */
public class IngredientParsedItemTypeAdapter extends TypeAdapter<List<IngredientParsedItem>> {
  @Override
  public void write(JsonWriter out, List<IngredientParsedItem> value) throws IOException {
    Gson gson = new Gson();
    out.beginArray();
    for (IngredientParsedItem ingredientParsedItem : value) {
      out.beginObject();
      out.name("parsed");
      out.beginArray();
      gson.getAdapter(IngredientParsedItem.class).write(out, ingredientParsedItem);
      out.endArray();
      out.endObject();
    }
    out.endArray();
  }

  @Override
  public List<IngredientParsedItem> read(JsonReader in) throws IOException {
    /*
     * Given an "ingredients" list, we convert this list of ingredientItem's to IngredientParsedItem
     * */
    List<IngredientParsedItem> ingredientParsedItemList = new ArrayList<>();
    in.beginArray();
    Gson gson = new Gson();
    while (in.hasNext()) {
      in.beginObject();
      JsonToken token = in.peek();
      if (token.equals(JsonToken.NAME) && in.nextName().equalsIgnoreCase("parsed")) {
        in.beginArray();
        token = in.peek();
        if (token.equals(JsonToken.BEGIN_OBJECT)) {
          while (in.hasNext()) {
            ingredientParsedItemList.add(
                gson.fromJson(in, new TypeToken<IngredientParsedItem>() {}.getType()));
          }
        }
        in.endArray();
      }

      in.endObject();
    }
    in.endArray();
    return ingredientParsedItemList;
  }
}
