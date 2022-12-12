package au.edu.sydney.soft3202.majorproject.model.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A thread singleton that provides a thread pool */
public class ThreadPoolSingleton {
  private static final int NUM_THREADS = Math.min(Runtime.getRuntime().availableProcessors(), 5);
  private static ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

  public static synchronized ExecutorService getInstance() {
    if (pool == null) {
      pool = Executors.newFixedThreadPool(NUM_THREADS);
    }
    return pool;
  }
}
