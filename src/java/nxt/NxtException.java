package nxt;

public abstract class NxtException extends Exception {

    protected NxtException() {
        super();
    }

    protected NxtException(String message) {
        super(message);
    }

    protected NxtException(String message, Throwable cause) {
        super(message, cause);
    }

    protected NxtException(Throwable cause) {
        super(cause);
    }

    public static class ValidationException extends NxtException {

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
