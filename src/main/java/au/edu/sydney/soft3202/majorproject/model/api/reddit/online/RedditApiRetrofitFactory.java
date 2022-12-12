package au.edu.sydney.soft3202.majorproject.model.api.reddit.online;

import au.edu.sydney.soft3202.majorproject.model.api.RetrofitFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/** Retrofit factory that provides a header to the retrofit creation if there is any */
public class RedditApiRetrofitFactory implements RetrofitFactory {
  private final Retrofit.Builder retrofitBuilder;

  /** To add header to every single request */
  private Interceptor headerInterceptor;

  private final OkHttpClient.Builder clientBuilder;

  public RedditApiRetrofitFactory(RedditApiAccessType redditApiAccessType) {
    this.clientBuilder =
        new OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS);
    this.retrofitBuilder =
        new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(redditApiAccessType.getBaseUrl());
  }

  private void buildRetrofit() {
    this.retrofitBuilder.client(clientBuilder.build());
  }

  public Interceptor getHeaderInterceptor() {
    return headerInterceptor;
  }

  public void setHeaderInterceptor(Interceptor headerInterceptor) {
    this.headerInterceptor = headerInterceptor;
    this.clientBuilder.interceptors().clear();
    this.clientBuilder.addInterceptor(headerInterceptor);
  }

  @Override
  public Retrofit getRetrofit() {
    buildRetrofit();
    return this.retrofitBuilder.build();
  }
}
