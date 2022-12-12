package au.edu.sydney.soft3202.majorproject.model.api.pastebin.online;

import au.edu.sydney.soft3202.majorproject.model.api.RetrofitFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

/** Retrofit factory for online pastebin api call */
public class PastebinApiRetrofitFactory implements RetrofitFactory {
  private final Retrofit.Builder retrofitBuilder;
  public static final String PASTEBIN_BASE_URL = "https://pastebin.com/";

  /** To add header to every single request */
  private final OkHttpClient.Builder clientBuilder;

  public PastebinApiRetrofitFactory() {
    this.clientBuilder =
        new OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS);
    this.retrofitBuilder =
        new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(PASTEBIN_BASE_URL)
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
