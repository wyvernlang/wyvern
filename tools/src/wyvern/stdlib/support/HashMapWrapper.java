package wyvern.stdlib.support;

import java.util.HashMap;

public class HashMapWrapper {
  public static HashMapWrapper hashmapwrapper = new HashMapWrapper();

  public HashMapWrapper() {
  }

  public <K, V> Object makeHashMap() {
    return new HashMap<K, V>();
  }

}
