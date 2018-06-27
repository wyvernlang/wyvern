package wyvern.stdlib.support;

import java.util.ArrayList;

import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;

public class ThreadWrapper {
  public static final ThreadWrapper threadwrapper = new ThreadWrapper();

  private class WyvernRunnable implements Runnable {
    private ObjectValue value = null;

    WyvernRunnable(ObjectValue value) {
      this.value = value;
    }

    public void run() {
      value.invoke("apply", new ArrayList<Value>()).executeIfThunk();
    }
  }

  public Thread makeThread(ObjectValue value) {
    return new Thread(new WyvernRunnable(value));
  }

  public int activeCount() {
    return Thread.activeCount();
  }

  public boolean interrupted() {
    return Thread.interrupted();
  }

  public void sleep(int m, int n) throws InterruptedException {
    if (n < 0) {
      Thread.sleep(m);
    } else {
      Thread.sleep(m, n);
    }
  }

  public Thread currentThread() {
    return Thread.currentThread();
  }

  public void join(Thread t) throws InterruptedException {
    t.join();
  }
}
