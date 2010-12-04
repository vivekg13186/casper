package com.casper;

class ErrorDispatcher {

    // Custom error handler
    private ErrorHandler errHandler;

    public ErrorDispatcher() {
        errHandler = new DefaultErrorHandler();
    }

    public void casperError(String errCode) throws CasperException {
        dispatch(null, errCode, null, null);
    }

    public void casperError(Mark where, String errCode) throws CasperException {
        dispatch(where, errCode, null, null);
    }

    public void casperError(Node n, String errCode) throws CasperException {
        dispatch(n.getStart(), errCode, null, null);
    }

    public void casperError(String errCode, String arg) throws CasperException {
        dispatch(null, errCode, new Object[]{arg}, null);
    }

    public void casperError(Mark where, String errCode, String arg)
            throws CasperException {
        dispatch(where, errCode, new Object[]{arg}, null);
    }

    public void casperError(Node n, String errCode, String arg)
            throws CasperException {
        dispatch(n.getStart(), errCode, new Object[]{arg}, null);
    }

    public void casperError(String errCode, String arg1, String arg2)
            throws CasperException {
        dispatch(null, errCode, new Object[]{arg1, arg2}, null);
    }

    public void casperError(String errCode, String arg1, String arg2, String arg3)
            throws CasperException {
        dispatch(null, errCode, new Object[]{arg1, arg2, arg3}, null);
    }

    public void casperError(Mark where, String errCode, String arg1, String arg2)
            throws CasperException {
        dispatch(where, errCode, new Object[]{arg1, arg2}, null);
    }

    public void casperError(Mark where, String errCode, String arg1, String arg2,
            String arg3)
            throws CasperException {
        dispatch(where, errCode, new Object[]{arg1, arg2, arg3}, null);
    }

    public void casperError(Node n, String errCode, String arg1, String arg2)
            throws CasperException {
        dispatch(n.getStart(), errCode, new Object[]{arg1, arg2}, null);
    }

    public void casperError(Node n, String errCode, String arg1, String arg2,
            String arg3)
            throws CasperException {
        dispatch(n.getStart(), errCode, new Object[]{arg1, arg2, arg3}, null);
    }

    public void casperError(Exception e) throws CasperException {
        dispatch(null, null, null, e);
    }

    public void casperError(String errCode, String arg, Exception e)
            throws CasperException {
        dispatch(null, errCode, new Object[]{arg}, e);
    }

    public void casperError(Node n, String errCode, String arg, Exception e)
            throws CasperException {
        dispatch(n.getStart(), errCode, new Object[]{arg}, e);
    }

    private void dispatch(Mark where, String errCode, Object[] args,
            Exception e) throws CasperException {
        String errMsg = null;
        int line = -1;
        int column = -1;
        boolean hasLocation = false;

        // Localize
        if (errCode != null) {
            errMsg = Localizer.getMessage(errCode, args);
        } else if (e != null) {
            // give a hint about what's wrong
            errMsg = e.getMessage();
        }

        // Get error location
        if (where != null) {
            column = where.getColumnNumber();
            hasLocation = true;
        }


        if (hasLocation) {
            errHandler.casperError(line, column, errMsg, e);
        } else {
            errHandler.casperError(errMsg, e);
        }
    }

    static class ErrorVisitor extends Node.Visitor {

        // Javascript source line number to be mapped
        private int lineNum;
        Node found;

        /*
         * Constructor.
         *
         * @param lineNum Source line number in the generated servlet code
         */
        public ErrorVisitor(int lineNum) {

            this.lineNum = lineNum;
        }

        public void doVisit(Node n) throws CasperException {
            if ((lineNum >= n.getBeginJavaLine())
                    && (lineNum < n.getEndJavaLine())) {
                found = n;
            }
        }
    }
}
