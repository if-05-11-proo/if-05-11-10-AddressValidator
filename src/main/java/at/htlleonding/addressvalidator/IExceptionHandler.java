package at.htlleonding.addressvalidator;

/**
 * The interface for exception handler.
 */
public interface IExceptionHandler {

	/**
	 * This method invoked to notify the exception handler to handle an exception.
	 * @param e The exception to handle.
	 */
	public void handle(Exception e);
}
