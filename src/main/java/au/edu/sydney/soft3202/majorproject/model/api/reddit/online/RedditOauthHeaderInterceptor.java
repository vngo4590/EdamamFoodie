package au.edu.sydney.soft3202.majorproject.model.api.reddit.online;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Retrofit factory for OAUTH api that provides a header to the retrofit creation if there is any
 */
public class RedditOauthHeaderInterceptor implements Interceptor {
  private String oauthToken;

  public RedditOauthHeaderInterceptor() {
    this(null);
  }

  public RedditOauthHeaderInterceptor(String oauthToken) {
    this.oauthToken = oauthToken;
  }

  public void setOauthToken(String oauthToken) {
    this.oauthToken = oauthToken;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder requestBuilder =
        chain.request().newBuilder().addHeader("User-Agent", "Foodie/1.0.0");
    requestBuilder.addHeader("Authorization", "Bearer " + oauthToken);
    return chain.proceed(requestBuilder.build());
  }
}
