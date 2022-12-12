package au.edu.sydney.soft3202.majorproject.model.api.pastebin.offline;

import au.edu.sydney.soft3202.majorproject.model.api.ResponseCallbackListener;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinApiQueryBuilder;
import au.edu.sydney.soft3202.majorproject.model.api.pastebin.PastebinRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/** Offline repository for pastebin api call */
public class OfflinePastebinRepository implements PastebinRepository {
  private final PastebinApiQueryBuilder pastebinApiQueryBuilder;

  private static final Logger LOGGER = LogManager.getLogger(OfflinePastebinRepository.class);
  private final int threadSleepMillis;

  public OfflinePastebinRepository(int threadSleepMillis) {
    this.threadSleepMillis = threadSleepMillis;
    this.pastebinApiQueryBuilder = new PastebinApiQueryBuilder();
  }

  @Override
  public void submitPaste(
      Map<String, String> query, ResponseCallbackListener<String> responseCallbackListener) {
    LOGGER.debug("Submitting call to offline Pastebin API ...");
    try {
      Thread.sleep(threadSleepMillis);
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
    responseCallbackListener.onSuccess("[Dummy Pastebin link]");
  }

  @Override
  public PastebinApiQueryBuilder getPastebinApiQueryBuilder() {
    return this.pastebinApiQueryBuilder;
  }
}
