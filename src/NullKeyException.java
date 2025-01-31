/**
 * Exceptions that indicate that a key is null.
 *
 * @author Samuel A. Rebelsky
 * @author Andrew N. Fargo
 */
public class NullKeyException extends Exception {
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new exception.
   */
  public NullKeyException() {
    super("key is null");
  } // NullKeyException()

  /**
   * Create a new exception with a particular message.
   *
   * @param message
   *   The exception's message.
   */
  public NullKeyException(String message) {
    super(message);
  } // NullKeyException(String)
} // class NullKeyException
