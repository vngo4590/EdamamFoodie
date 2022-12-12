package au.edu.sydney.soft3202.majorproject.model.api;

import au.edu.sydney.soft3202.majorproject.model.entity.edamam.nutrients.NutrientsResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.edamam.parser.FoodParserResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.authorization.AuthorizationResponse;
import au.edu.sydney.soft3202.majorproject.model.entity.reddit.submission.SubmissionParentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Objects;

public enum DummyURLEnum {
  REDDIT_SUBMISSION_PARENT_RESPONSE(
      Objects.requireNonNull(
          DummyURLEnum.class.getResource(
              "/au/edu/sydney/soft3202/majorproject/dummy/dummy-reddit-oath-post.json")),
      new TypeToken<SubmissionParentResponse>() {}.getType()),

  REDDIT_AUTHORIZATION_RESPONSE(
      Objects.requireNonNull(
          DummyURLEnum.class.getResource(
              "/au/edu/sydney/soft3202/majorproject/dummy/dummy-reddit-access.json")),
      new TypeToken<AuthorizationResponse>() {}.getType()),
  EDAMAM_FOOD_PARSER_RESPONSE(
      Objects.requireNonNull(
          DummyURLEnum.class.getResource(
              "/au/edu/sydney/soft3202/majorproject/dummy/dummy-food-parser.json")),
      new TypeToken<FoodParserResponse>() {}.getType()),

  EDAMAM_NUTRIENTS_RESPONSE(
      Objects.requireNonNull(
          DummyURLEnum.class.getResource(
              "/au/edu/sydney/soft3202/majorproject/dummy/dummy-nutrient-response.json")),
      new TypeToken<NutrientsResponse>() {}.getType()),
  ;
  private final URL url;
  private final Type type;

  DummyURLEnum(URL url, Type type) {
    this.url = url;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public URL getUrl() {
    return url;
  }

  public static Object loadFromJson(DummyURLEnum urlEnum) throws IOException {
    URL resource = urlEnum.getUrl();
    Type classType = urlEnum.getType();
    Gson gson = new Gson();
    JsonReader reader = new JsonReader(new InputStreamReader(resource.openStream()));
    return gson.fromJson(reader, classType);
  }
}
