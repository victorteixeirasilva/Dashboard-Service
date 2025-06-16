package tech.inovasoft.inevolving.ms.dashboard.domain.exception;

public class ExternalServiceErrorException extends Exception {
    public ExternalServiceErrorException(String s) {
        super("Error in the " + s );
    }
}
