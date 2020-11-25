package uy.gub.agesic.pdi.pys.backoffice.utiles.exceptions;

public class BackofficeException extends Exception {

    public BackofficeException() {
    }

    public BackofficeException(String message) {
        super(message);
    }

    public BackofficeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackofficeException(Throwable cause) {
        super(cause);
    }

    public BackofficeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
