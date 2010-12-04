package com.casper;

interface ErrorHandler {

      /**
     * Processes the given Casper error.
     * @param line Parse error line number
     * @param column Parse error column number
     * @param msg Parse error message
     * @param exception Parse exception
     */
    public void casperError(int line, int column, String msg,
            Exception exception) throws CasperException;

    /**
     * Processes the given Casper parse error.
     * @param msg Parse error message
     * @param exception Parse exception
     */
    public void casperError(String msg, Exception exception)
            throws CasperException;

}
