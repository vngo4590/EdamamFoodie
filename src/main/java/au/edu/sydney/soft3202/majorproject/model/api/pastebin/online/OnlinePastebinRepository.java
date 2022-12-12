package au.edu.sydney.soft3202.majorproject.model.api.pastebin.online;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiService;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;

import java.util.Map;

/** Online repository for pastebin api call */
public class OnlinePastebinRepository implements PastebinRepository {
  private final PastebinApiQueryBuilder pastebinApiQueryBuilder;
  private final PastebinApiService pastebinApiService;
  private static final Logger LOGGER = LogManager.getLogger(OnlinePastebinRepository.class);

  public OnlinePastebinRepository(PastebinApiService pastebinApiService) {
    this.pastebinApiService = pastebinApiService;
    this.pastebinApiQueryBuilder = new PastebinApiQueryBuilder();
  }

  @Override
  public PastebinApiQueryBuilder getPastebinApiQueryBuilder() {
    return pastebinApiQueryBuilder;
  }

  @Override
  public void submitPaste(
      Map<String, String> query, ResponseCallbackListener<String> responseCallbackListener) {
    LOGGER.debug("Submitting call to Pastebin API...");
    Call<String> authResponseCall = this.pastebinApiService.submitPaste(query);
    authResponseCall.enqueue(responseCallbackListener);
  }
}
