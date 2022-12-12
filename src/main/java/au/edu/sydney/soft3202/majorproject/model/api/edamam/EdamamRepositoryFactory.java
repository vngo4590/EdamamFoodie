package au.edu.sydney.soft3202.majorproject.model.api.edamam;

import au.edu.sydney.soft3202.majorproject.model.api.edamam.offline.OfflineEdamamFoodRepository;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.online.EdamamFoodApiRetrofitFactory;
import au.edu.sydney.soft3202.majorproject.model.api.edamam.online.OnlineEdamamFoodRepository;
import au.edu.sydney.soft3202.majorproject.model.db.NutrientDaoImpl;

/** Central Repository Factory for creating Online and Offline Edamam Repositories */
public class EdamamRepositoryFactory {
  public static EdamamRepository createOnlineFoodRepository() {
    EdamamRepository foodRepository;
    EdamamFoodApiRetrofitFactory retrofitFactory = new EdamamFoodApiRetrofitFactory();
    EdamamFoodApiService apiService =
        retrofitFactory.getRetrofit().create(EdamamFoodApiService.class);
    NutrientDaoImpl nutrientDao = new NutrientDaoImpl();
    nutrientDao.setUp();
    foodRepository = new OnlineEdamamFoodRepository(apiService, nutrientDao);
    return foodRepository;
  }

  public static EdamamRepository createOfflineFoodRepository() {
    EdamamRepository foodRepository;
    foodRepository = new OfflineEdamamFoodRepository(2000);
    return foodRepository;
  }
}
