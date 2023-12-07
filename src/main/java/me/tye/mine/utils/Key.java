package me.tye.mine.utils;

public enum Key {

  key(),
  filePath(),
  newLine(),
  member();

private String replaceWith = "";


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
}
