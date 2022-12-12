package au.edu.sydney.soft3202.majorproject.model.api.pastebin;

import au.edu.sydney.soft3202.majorproject.model.api.pastebin.offline.OfflinePastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.online.OnlinePastebinRepository;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.online.PastebinApiRetrofitFactory;

/** Central Repository Factory for creating Online and Offline Pastebin Repositories */
public class PastebinRepositoryFactory {
  public static PastebinRepository createOfflinePastebinRepository() {
    PastebinRepository pastebinRepository;
    pastebinRepository = new OfflinePastebinRepository(2000);
    return pastebinRepository;
  }

  public static PastebinRepository createOnlinePastebinRepository() {
    PastebinRepository pastebinRepository;
    PastebinApiRetrofitFactory pastebinApiRetrofitFactory = new PastebinApiRetrofitFactory();
    PastebinApiService pastebinApiService =
        pastebinApiRetrofitFactory.getRetrofit().create(PastebinApiService.class);
    pastebinRepository = new OnlinePastebinRepository(pastebinApiService);
    return pastebinRepository;
  }
}
