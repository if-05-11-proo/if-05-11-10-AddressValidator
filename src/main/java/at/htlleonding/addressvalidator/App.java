package at.htlleonding.addressvalidator;

import at.htlleonding.addressvalidator.sender.Sender;

/**
 * The 'Address Validator' application.
 */
public class App {

    /**
     * The exception handler of the application.
     */
    class AppExceptionHandler implements IExceptionHandler {
        @Override
        public void handle(Exception e) {
            // handles the exception that is thrown upon wrong API Tokens
            if (e instanceof ReceptionException) {
                System.out.printf("Error: %s%n", e.getMessage());
            } else {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** The singleton instance */
    private static App mMe;


    private App() {
			  /* TODO: Implement */
    }

    /**
     * Provides the singleton instance of the application.
     * @return The application instance.
     */
    public static App getMe() {
        if (mMe == null) {
            mMe = new App();
        }
        return mMe;
    }

    /**
     * Starts the reception of addresses from the sender.
     * The sender is a singleton, it's instance can be accessed via
     * {@code Sender.getMe()}. The receiver is instantiated herein and
     * supplied with all required objects, esp. listeners.
     * @param externalListener The extra listener to attach to the receiver, may be null.
     * @param exceptionHandler The handler for exceptions. Each caught exception shall be
     *                         handled by this handler.
     */
    public void startTransmission(IAddressListener externalListener, IExceptionHandler exceptionHandler) {
			  /* TODO: Implement */
    }

    /**
     * Provides the address supplier for phone numbers.
     * All received valid phone numbers are provided in the order they are received.
     * Invalid phone numbers are not provided.
     * @return The address supplier.
     */
    public IAddressSupplier getPhoneNumberSupplier() {
			  /* TODO: Implement */
        return null;
    }

    /**
     * Provides the address supplier for unique phone numbers.
     * No phone number is provided twice.
     * The phone numbers are provided in the order they are received.
     * @return The address supplier.
     */
    public IAddressSupplier getUniquePhoneNumberSupplier() {
			  /* TODO: Implement */
        return null;
    }

    /**
     * Provides the address supplier for e-mail addresses.
     * All received valid e-mail addresses are provided in the order they are received.
     * Invalid e-mail addresses are not provided.
     * @return The address supplier.
     */
    public IAddressSupplier getEMailAddressSupplier() {
			  /* TODO: Implement */
        return null;
    }

    /**
     * Provides the address supplier for unique e-mail addresses.
     * No e-mail address is provided twice.
     * The addresses are provided in the order they are received.
     * @return The address supplier.
     */
    public IAddressSupplier getUniqueEMailAddressSupplier() {
			  /* TODO: Implement */
        return null;
    }

    /**
     * Prints all addresses provided by the given supplier to the standard output.
     * @param supplier The supplier that provides the addresses to print out.
     * @param subject The subject of the table.
     */
    private void printAddresses(IAddressSupplier supplier, String subject) {
        System.out.printf("%n----------------------------------%n");
        System.out.printf("Received %s: %n", subject);
        String address = supplier.getAddress();
        while (address != null) {
            System.out.printf("\"%s\",%n", address);
            address = supplier.getAddress();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.startTransmission(null, app.new AppExceptionHandler());
        app.printAddresses(app.getPhoneNumberSupplier(), "phone numbers");
        app.printAddresses(app.getUniquePhoneNumberSupplier(), "unique phone numbers");
        app.printAddresses(app.getEMailAddressSupplier(), "e-mail addresses");
        app.printAddresses(app.getUniqueEMailAddressSupplier(), "unique e-mail addresses");
    }
}
