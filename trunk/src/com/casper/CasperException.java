package com.casper;

public class CasperException extends Exception {
    
    public CasperException(String reason) {
        super(reason);
    }

    /**
     * Creates a CasperException with the embedded exception and the reason for
     * throwing a CasperException
     */
    public CasperException (String reason, Throwable exception) {
        super(reason, exception);
    }

    /**
     * Creates a CasperException with the embedded exception
     */
    public CasperException (Throwable exception) {
        super(exception);
    }
}

