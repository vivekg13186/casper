package com.casper;

class DefaultErrorHandler implements ErrorHandler{

   /*
     * Processes the given Casper parse error.
     *
     * @param line Parse error line number
     * @param column Parse error column number
     * @param errMsg Parse error message
     * @param exception Parse exception
     */
    public void casperError(int line, int column, String errMsg,
            Exception ex) throws CasperException {
        throw new CasperException("(" + line + "," + column + ")"
                + " " + errMsg, ex);
    }

    /*
     * Processes the given Casper parse error.
     *
     * @param errMsg Parse error message
     * @param exception Parse exception
     */
    public void casperError(String errMsg, Exception ex) throws CasperException {
        throw new CasperException(errMsg, ex);
    }

}
