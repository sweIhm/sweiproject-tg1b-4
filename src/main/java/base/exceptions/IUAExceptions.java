package base.exceptions;

public abstract class IUAExceptions extends Exception {
    /**
     * Throws Exception with the given Message when called.
     * @param message String Message that should be given out.
     */
    public IUAExceptions(String message) {
        super(message);
    }
}