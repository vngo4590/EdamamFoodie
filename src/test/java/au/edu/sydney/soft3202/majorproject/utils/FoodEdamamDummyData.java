package au.edu.sydney.soft3202.majorproject.utils;

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

public class FoodEdamamDummyData {
    public static String getErrorResponseNoIngredients() {
        return """
                {
                    "error": "bad_request",
                    "message": "Expected exactly one of ingredient text (ingr) or upc"
                }
                """;
    }

    public static String getErrorResponsePostingTooFast() {
        return """
                {
                    "json": {
                        "ratelimit": 851.481175,
                        "errors": [
                            [
                                "RATELIMIT",
                                "Looks like you've been doing that a lot. Take a break for 14 minutes before trying again.",
                                "ratelimit"
                            ]
                        ]
                    }
                }
                """;
    }

    public static String getErrorResponsePostingToWrongSubReddit() {
        return """
                {
                    "json": {
                        "errors": [
                            [
                                "BAD_SR_NAME",
                                "This community name isn't recognizable. Check the spelling and try again.",
                                "sr"
                            ]
                        ]
                    }
                }
                """;
    }

    public static String getErrorResponsePostingNoContents() {
        return """
                {
                    "json": {
                        "errors": [
                            [
                                "NO_TEXT",
                                "we need something here"
                            ]
                        ]
                    }
                }
                """;
    }


    public static String getErrorResponsePostingWithInvalidToken() {
        return """
                {
                    "json": {
                        "errors": [
                            [
                                "USER_REQUIRED",
                                "Please log in to do that.",
                                "abc"
                            ]
                        ]
                    }
                }
                """;
    }

    public static SubmissionParentResponse parseSubmissionParentResponse(String value) {
        Type classType = (new TypeToken<SubmissionParentResponse>() {
        }.getType());
        Gson gson = new Gson();
        return gson.fromJson(value, classType);
    }

    public static AuthorizationResponse parseAuthorizationResponse(String value) {
        Type classType = (new TypeToken<AuthorizationResponse>() {
        }.getType());
        Gson gson = new Gson();
        return gson.fromJson(value, classType);
    }

    public static String getErrorResponseInvalidOauthPostError() {
        return """
                {
                    "error": "invalid_grant"
                }
                """;
    }

    public static FoodParserResponse getSampleData(String submissionText) {
        URL resource = Objects.requireNonNull(
                FoodEdamamDummyData
                        .class
                        .getResource("/au/edu/sydney/soft3202/majorproject/api/Simple_Ingredient_dummy_data.json")
        );
        Type classType = (new TypeToken<FoodParserResponse>() {
        }.getType());
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(resource.openStream()));
            FoodParserResponse foodParserResponse = gson.fromJson(reader, classType);
            foodParserResponse.setText(submissionText);
            return foodParserResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NutrientsResponse getNutrientsSampleData() {
        URL resource = Objects.requireNonNull(
                FoodEdamamDummyData
                        .class
                        .getResource("/au/edu/sydney/soft3202/majorproject/api/Simple_nutrient_dummy_data.json")
        );
        Type classType = (new TypeToken<NutrientsResponse>() {
        }.getType());
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(resource.openStream()));
            return gson.fromJson(reader, classType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNutrientsRawSampleData() {
        return new Gson().toJson(getNutrientsSampleData());
    }

    public static AuthorizationResponse getRedditAuthorizationResponse() {
        URL resource = Objects.requireNonNull(
                FoodEdamamDummyData
                        .class
                        .getResource("/au/edu/sydney/soft3202/majorproject/api/test-reddit-access.json")
        );
        Type classType = (new TypeToken<AuthorizationResponse>() {
        }.getType());
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(resource.openStream()));
            return gson.fromJson(reader, classType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SubmissionParentResponse getRedditSubmissionResponse() {
        URL resource = Objects.requireNonNull(
                FoodEdamamDummyData
                        .class
                        .getResource("/au/edu/sydney/soft3202/majorproject/api/test-reddit-oath-post.json")
        );
        Type classType = (new TypeToken<SubmissionParentResponse>() {
        }.getType());
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = new JsonReader(new InputStreamReader(resource.openStream()));
            return gson.fromJson(reader, classType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
