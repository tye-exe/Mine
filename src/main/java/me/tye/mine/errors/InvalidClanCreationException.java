package me.tye.mine.errors;

/**
 An exception to be thrown when a player tries to create an invalid clan.
 */
public class InvalidClanCreationException extends Exception {

/**
 An exception to be thrown when a player tries to create an invalid clan.
 * @param message Why the clan is invalid.
 */
public InvalidClanCreationException(String message) {
   super(message);
}

}
