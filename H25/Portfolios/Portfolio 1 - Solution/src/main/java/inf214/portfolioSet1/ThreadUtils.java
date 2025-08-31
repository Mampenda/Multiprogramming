package inf214.portfolioSet1;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadUtils {
  public static void startAll(List<? extends Thread> ts) {
    for (var t : ts) {
      t.start();
    }
  }

  public static void waitForAll(List<? extends Thread> ts) {
    for (var t : ts) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * This is just here to catch some common problems, including deadlocks and
   * endless waiting for the barrier.
   * 
   * The watchdog will check for trouble every 5 seconds, and abort the program
   * with an error message if it detects a deadlock situation.
   *
   */
  public static class WatchDog implements AutoCloseable {
    private final WatchDogThread thread;
    private final Lock lock;

    public WatchDog() {
      this(null, null);
    }

    public WatchDog(List<? extends Thread> threads, CyclicBarrier barrier) {
      // we'll keep this lock until someone calls close() on the watchdog
      lock = new ReentrantLock();
      lock.lock();
      // a separate thread will wake up every 5 seconds and check for trouble
      thread = new WatchDogThread(lock, threads, barrier);
      thread.start();

    }

    public WatchDog(List<? extends Thread> threads) {
      this(threads, null);
    }

    @Override
    public void close() throws InterruptedException {
      // main program is done, shut down the watchdog
      lock.unlock();
      thread.join();
    }

    static class WatchDogThread extends Thread {
      private final List<? extends Thread> threads;
      private final CyclicBarrier barrier;
      private final Lock lock;
      private final ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
      private int timedDeadlock = 0;
      private List<Long> lockOwners;

      public WatchDogThread(Lock lock, List<? extends Thread> threads, CyclicBarrier barrier) {
        super("watchdog");
        this.lock = lock;
        this.threads = threads;
        this.barrier = barrier;
      }

      public void run() {
        while (true) {
          try {
            // try to acquire the lock, waiting max 5 seconds
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
              // if we succeeded, the main program is done and so are we
              return;
            } else {
              // main program is still running, so check for trouble
              // System.out.println("Checking for deadlocks");
              runCheck();
            }
          } catch (InterruptedException e) {
          }
        }
      }

      protected void runCheck() {
        // Check that all the parties to the barrier are still alive
        if (barrier != null && threads != null //
            && this.barrier.getNumberWaiting() > 0 //
            && threads.stream().anyMatch(thread -> !thread.isAlive())) {
          System.err.println("Some threads are waiting for other threads that have terminated! Breaking barrier...");
          barrier.reset(); // will send BarrierBrokenException to waiting threads
        }

        long[] ids = tmx.findDeadlockedThreads();
        if (ids != null) { // deadlock detected
          dumpDeadlocks(ids);
          System.exit(1);
        }

        if (threads != null && threads.stream()
            .allMatch(thread -> thread.getState() == State.WAITING || thread.getState() == State.BLOCKED)) {
          System.err.println("All threads are WAITING or BLOCKED – we're probably deadlocked!");
          System.exit(1);
        }
        System.err.println(threads.stream().map(t -> t.getName() + ":" + t.getState()).toList());
        List<Long> owners = threads.stream().map(t -> tmx.getThreadInfo(t.getId()))
            .map(ti -> ti != null ? ti.getLockOwnerId() : null).toList();
        if (lockOwners != null && timedDeadlock > 0 && !lockOwners.equals(owners)) {
          timedDeadlock = 0;
          System.err.println("Lock owners changed, we're probably not deadlocked");
        }
        lockOwners = owners;

        if (threads != null && threads.stream().allMatch(thread -> thread.getState() == State.WAITING //
            || thread.getState() == State.BLOCKED //
            || thread.getState() == State.TIMED_WAITING)) {
          if (timedDeadlock < 3) {
            System.err.println(
                "All threads are WAITING, BLOCKED or TIMED_WAITING – are we deadlocked? [" + timedDeadlock + "]");
            timedDeadlock++;
          } else {
            System.err.println("All threads are WAITING, BLOCKED  or TIMED_WAITING – we're probably deadlocked!");
            System.exit(1);
          }
        } else if (timedDeadlock > 0) {
          System.err.println("Something's happening, we're probably not deadlocked anyway");
          timedDeadlock = 0;
        }
      }

      protected void dumpDeadlocks(long[] ids) {
        ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
        for (var info : infos) {
          System.err.println(info);
        }
      }
    }
  }

  public static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }

  public static <T> T ignoreInterrupted(InterruptibleSupplier<T> call) {
    try {
      return call.get();
    } catch (InterruptedException e) {
      return null;
    }
  }

}
