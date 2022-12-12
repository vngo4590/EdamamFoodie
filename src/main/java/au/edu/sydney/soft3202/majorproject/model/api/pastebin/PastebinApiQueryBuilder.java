package au.edu.sydney.soft3202.majorproject.model.api.pastebin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/** Query builder for the pastebin api */
public class PastebinApiQueryBuilder {
  private Map<String, String> queryMap;
  private static final String API_DEV_KEY = "api_dev_key";
  private static final String API_OPTION_KEY = "api_option";
  private static final String API_PASTE_CODE_KEY = "api_paste_code";
  private static final Logger LOGGER = LogManager.getLogger(PastebinApiQueryBuilder.class);

  public PastebinApiQueryBuilder() {
    reset();
  }

  public void reset() {
    this.queryMap = new HashMap<>();
    this.queryMap.put(API_DEV_KEY, System.getenv("PASTEBIN_API_KEY"));
    this.queryMap.put(API_OPTION_KEY, "paste");
    LOGGER.debug("All Pastebin Queries have been reset");
  }

  public PastebinApiQueryBuilder setPasteContent(String content) throws IllegalArgumentException {
    this.queryMap.put(API_PASTE_CODE_KEY, content);
    LOGGER.debug("All Pastebin content has seen set: " + content);
    return this;
  }

  public Map<String, String> build() {
    return queryMap;
  }
}
