package au.edu.sydney.soft3202.majorproject.model.api.reddit;

import au.edu.sydney.soft3202.majorproject.model.api.ApiMode;
/** Singleton that provides global access to Reddit Repository */
public class RedditRepositorySingleton {
  private static RedditRepository redditRepository;

  /**
   * Initialize the instance of the RedditRepository. If no api mode is provided, the default will
   * be online
   *
   * @param apiMode the api mode of the repository. Null for default
   */
  public static void init(ApiMode apiMode) {
    if (apiMode == ApiMode.OFFLINE) {
      redditRepository = RedditRepositoryFactory.createOfflineRedditRepository();
    } else {
      redditRepository = RedditRepositoryFactory.createOnlineRedditRepository();
    }
  }
  /**
   * Get the instance of the RedditRepository. If no instance of the repository has been
   * initialized, this method will create the object using the default api mode.
   *
   * @return the instance of RedditRepository
   */
  public synchronized static RedditRepository getInstance() {
    if (redditRepository == null) {
      init(null);
    }
    return redditRepository;
  }
}
