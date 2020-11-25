package uy.gub.agesic.pdi.pys.backend.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PSException extends Exception {

    private final String message;

    private final String description;

    private final String code;

    public PSException(String message, String description) {
        code = "Error Interno";
        this.message = message;
        this.description = description;
    }

    public PSException(String message, String description, String code, Throwable e) {
        super(e);
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public PSException(Exception e) {
        this((Throwable) e);
    }

    public PSException(Throwable t) {
        if (t instanceof PSException) {
            PSException srException = (PSException) t;
            this.code = srException.getCode();
            this.description = srException.getDescription();
            this.message = srException.getMessage();
        } else {
            code = "Error Interno";
            message = t.getMessage();

            StringWriter errors = new StringWriter();
            t.printStackTrace(new PrintWriter(errors));
            description = errors.toString();
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

}
