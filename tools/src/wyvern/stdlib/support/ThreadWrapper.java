package wyvern.stdlib.support;

import java.math.BigInteger;
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

  public Object bigIntToLong(BigInteger m) {
    return m.longValue();
  }

  public void sleep(long milli) throws InterruptedException {
    Thread.sleep(milli);
  }

  public Object makeThread(ObjectValue value) {
    return new Thread(new WyvernRunnable(value));
  }

  public boolean interrupted() {
    return Thread.interrupted();
  }

  public Object currentThread() {
    return Thread.currentThread();
  }
}
