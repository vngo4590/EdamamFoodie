package au.edu.sydney.soft3202.majorproject.model.api.edamam.online;

import au.edu.sydney.soft3202.majorproject.model.api.RetrofitFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * A factory class that produces the instance of retrofit that is used for Online Edamam Api calls
 */
public class EdamamFoodApiRetrofitFactory implements RetrofitFactory {
  private final Retrofit.Builder retrofitBuilder;
  public static final String EDANAM_FOOD_BASE_URL = "https://api.edamam.com/";
  private final OkHttpClient.Builder clientBuilder;

  public EdamamFoodApiRetrofitFactory() {
    this.clientBuilder =
        new OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS);
    this.retrofitBuilder =
        new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(EDANAM_FOOD_BASE_URL)
            .client(clientBuilder.build());
  }

  private void buildRetrofit() {
    this.retrofitBuilder.client(clientBuilder.build());
  }

  @Override
  public Retrofit getRetrofit() {
    // Interceptor may change along the way, so we need to make sure
    // We get the right retrofit build
    buildRetrofit();
    return retrofitBuilder.build();
  }
}
