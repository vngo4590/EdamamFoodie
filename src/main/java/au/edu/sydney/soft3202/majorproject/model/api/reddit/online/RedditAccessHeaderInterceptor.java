package au.edu.sydney.soft3202.majorproject.model.api.reddit.online;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/** Interceptor to add the authorization token to the header for reddit response call */
public class RedditAccessHeaderInterceptor implements Interceptor {
  private static final String APP_ID = System.getenv("REDDIT_APP_ID");
  private static final String APP_SECRET = System.getenv("REDDIT_APP_SECRET");
  private final String basicToken;

  public RedditAccessHeaderInterceptor() {
    this.basicToken = Credentials.basic(APP_ID, APP_SECRET);
  }

  @Override
  public Response intercept(Interceptor.Chain chain) throws IOException {
    Request.Builder requestBuilder =
        chain.request().newBuilder().addHeader("User-Agent", "Foodie/1.0.0");
    requestBuilder.addHeader("Authorization", basicToken);
    return chain.proceed(requestBuilder.build());
  }
}
