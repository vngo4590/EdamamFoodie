package au.edu.sydney.soft3202.majorproject.model.api.pastebin;

import au.edu.sydney.soft3202.majorproject.model.api.ApiMode;

/** Singleton that provides global access to Pastebin Repository */
public class PastebinRepositorySingleton {
  private static PastebinRepository pastebinRepository;

  /**
   * Initialize the instance of the PastebinRepository. If no api mode is provided, the default will
   * be online
   *
   * @param apiMode the api mode of the repository. Null for default
   */
  public static void init(ApiMode apiMode) {
    if (apiMode == ApiMode.OFFLINE) {
      pastebinRepository = PastebinRepositoryFactory.createOfflinePastebinRepository();
    } else {
      pastebinRepository = PastebinRepositoryFactory.createOnlinePastebinRepository();
    }
  }
  /**
   * Get the instance of the PastebinRepository. If no instance of the repository has been
   * initialized, this method will create the object using the default api mode.
   *
   * @return the instance of PastebinRepository
   */
  public synchronized static PastebinRepository getInstance() {
    if (pastebinRepository == null) {
      init(null);
    }
    return pastebinRepository;
  }
}
