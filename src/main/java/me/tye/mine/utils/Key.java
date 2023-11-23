package me.tye.mine.utils;

public enum Key {

  key("key"),
  player("player"),
  filePath("filePath");

private final String valueToReplace;
private String replaceWith = "";

Key(String valueToReplace) {
  this.valueToReplace = valueToReplace;
}


/**
 * @param string The string to replace with value with.
 * @return The modified key object.
 */
public Key replaceWith(String string) {
  this.replaceWith = string;
  return this;
}

/**
 * @return The string to replace with.
 */
public String getReplaceWith() {
  return replaceWith;
}

/**
 * @return The value to replace.
 */
@Override
public String toString() {
  return this.valueToReplace;
}
}
