package me.tye.mine.errors;

/**
 A RuntimeException to be thrown when accessing the database fails in such a way that the state of the data cannot be assured.
 */
public class FatalDatabaseException extends RuntimeException {

public FatalDatabaseException() {
}

public FatalDatabaseException(String message) {
  super(message);
}

public FatalDatabaseException(String message, Throwable cause) {
  super(message, cause);
}

public FatalDatabaseException(Throwable cause) {
  super(cause);
}

public FatalDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
  super(message, cause, enableSuppression, writableStackTrace);
}

}
