package me.tye.mine.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public @Nullable Object put(@NotNull Object key, @NotNull Object value) throws NullPointerException {
  if (value == null) throw new NullPointerException("Value is null");

  return super.put(key, value);
}

}
