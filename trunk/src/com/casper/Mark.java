package com.casper;

/**
 *Capser code position marker
 * 
 */
class Mark {

    // position within current stream
    int cursor, line, col;
    // current stream
    char[] stream = null;
    // reader that owns this mark (so we can look up fileid's)
    private CasperReader reader;

    /**
     * Constructor
     * @param reader CasperReader this mark belongs to
     * @param inStream current stream for this mark
     */
    Mark(CasperReader reader, char[] inStream) {

        this.reader = reader;
        this.stream = inStream;
        this.cursor = 0;
        this.line = 1;
        this.col = 1;
    }

    /**
     * Constructor
     */
    Mark(Mark other) {

        this.reader = other.reader;
        this.stream = other.stream;
        this.cursor = other.cursor;
        this.line = other.line;
        this.col = other.col;
    }

    /**
     * Constructor
     */
    Mark(int line, int col) {

        this.reader = null;
        this.stream = null;
        this.cursor = 0;
        this.line = line;
        this.col = col;
    }

    // -------------------- Locator interface --------------------
    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return col;
    }

    @Override
    public String toString() {
        return "(" + line + "," + col + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Mark) {
            Mark m = (Mark) other;
            return this.reader == m.reader
                    && this.cursor == m.cursor && this.line == m.line
                    && this.col == m.col;
        }
        return false;
    }


    /**
     * Sets this mark's state to a new stream.
     * It will store the current stream in it's includeStack.
     *
     * @param inStream new stream for mark
     */
    public void pushStream(char[] inStream)
    {
        // set new variables
        cursor = 0;
        line = 1;
        col = 1;
        stream = inStream;
    }
}
