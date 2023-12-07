package me.tye.mine.utils;

import java.util.HashMap;

public class MineCacheMap<K, V> extends HashMap {

/**
 Puts an object into this HashMap.
 * @param key Key to put into the HashMap.
 * @param value Value to put into the HashMap.
 * @return The object replaced.
 * @throws NullPointerException If the value passed is null.
 */
@Override
public Object put(Object key, Object value) throws NullPointerException {
  if (value == null) throw new NullPointerException("Value is null");

  return super.put(key, value);
}

}
