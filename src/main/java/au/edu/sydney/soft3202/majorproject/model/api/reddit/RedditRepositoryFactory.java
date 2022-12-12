package au.edu.sydney.soft3202.majorproject.model.api.reddit;

import au.edu.sydney.soft3202.majorproject.model.api.reddit.offline.OfflineRedditRepository;
import au.edu.sydney.soft3202.majorproject.model.api.reddit.online.*;
import okhttp3.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Central Repository Factory for creating Online and Offline Reddit Repositories */
public class RedditRepositoryFactory {
  private static final Logger LOGGER = LogManager.getLogger(RedditRepositoryFactory.class);

  public static RedditRepository createOnlineRedditRepository() {
    /*
     * Creating Reddit Access Service
     * */
    RedditApiRetrofitFactory accessRetrofitFactory =
        new RedditApiRetrofitFactory(RedditApiAccessType.ACCESS);
    Interceptor accessInterceptor = new RedditAccessHeaderInterceptor();
    accessRetrofitFactory.setHeaderInterceptor(accessInterceptor);
    RedditAccessApiService redditAccessApiService =
        accessRetrofitFactory.getRetrofit().create(RedditAccessApiService.class);
    /*
     * Creating Reddit Oauth Service
     * */
    RedditApiRetrofitFactory oauthRetrofitFactory =
        new RedditApiRetrofitFactory(RedditApiAccessType.OAUTH);
    RedditOauthHeaderInterceptor oauthInterceptor = new RedditOauthHeaderInterceptor();
    oauthRetrofitFactory.setHeaderInterceptor(oauthInterceptor);
    RedditOauthApiService redditOauthApiService =
        oauthRetrofitFactory.getRetrofit().create(RedditOauthApiService.class);
    OnlineRedditRepository onlineRedditRepository =
        new OnlineRedditRepository(redditAccessApiService, redditOauthApiService, null);
    RedditTokenAccessObserver redditTokenAccessObserver =
        token -> {
          // Update the header as soon as we got the token
          LOGGER.debug("Got Auth token.");
          if (token != null) {
            synchronized (onlineRedditRepository) {
              oauthInterceptor.setOauthToken(token);
              onlineRedditRepository.setRedditAccessState(RedditAccessState.LOGGED_IN);
              onlineRedditRepository.setRedditOauthApiService(
                  oauthRetrofitFactory.getRetrofit().create(RedditOauthApiService.class));
              LOGGER.debug("User has logged in.");
            }
          } else {
            LOGGER.debug("Invalid token. User not logged in.");
          }
        };
    onlineRedditRepository.setRedditTokenAccessObserver(redditTokenAccessObserver);
    return onlineRedditRepository;
  }

  public static RedditRepository createOfflineRedditRepository() {
    return new OfflineRedditRepository(2000);
  }
}
