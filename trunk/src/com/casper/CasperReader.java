/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casper;

import java.io.CharArrayWriter;
import java.io.InputStreamReader;

/**
 *
 * @author vivek
 */
class CasperReader {

    /**
     * The current spot in the file.
     */
    private Mark current;
    /**
     * The Jasper error dispatcher.
     */
    private ErrorDispatcher err;

    public ErrorDispatcher getErrorDispatcher(){
        return this.err;
    }
 
    /**
     * Constructor: same as above constructor but with initialized reader
     * to the file given.
     */
    public CasperReader(InputStreamReader reader, ErrorDispatcher err) throws CasperException {

        this.err = err;


        try {
            CharArrayWriter caw = new CharArrayWriter();
            char buf[] = new char[1024];
            for (int i = 0; (i = reader.read(buf)) != -1;) {
                caw.write(buf, 0, i);
            }
            caw.close();
            if (current == null) {
                current = new Mark(this, caw.toCharArray());
            } else {
                current.pushStream(caw.toCharArray());
            }
        } catch (Throwable ex) {
            err.casperError("casper.error.file.cannot.read");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception any) {
                }
            }
        }
    }

    /**
     * Checks if the current file has more input.
     *
     * @return True if more reading is possible
     * @throws CasperException if an error occurs
     */
    boolean hasMoreInput() throws CasperException {
        if (current.cursor >= current.stream.length) {
            return false;
        }

        return true;
    }

    int nextChar() throws CasperException {
        if (!hasMoreInput()) {
            return -1;
        }

        int ch = current.stream[current.cursor];

        current.cursor++;

        if (ch == '\n') {
            current.line++;
            current.col = 0;
        } else {
            current.col++;
        }
        return ch;
    }

    /**
     * Back up the current cursor by one char, assumes current.cursor > 0,
     * and that the char to be pushed back is not '\n'.
     */
    void pushChar() {
        current.cursor--;
        current.col--;
    }

    String getText(Mark start, Mark stop) throws CasperException {
        Mark oldstart = mark();
        reset(start);
        CharArrayWriter caw = new CharArrayWriter();
        while (!stop.equals(mark())) {
            caw.write(nextChar());
        }
        caw.close();
        reset(oldstart);
        return caw.toString();
    }

    int peekChar() throws CasperException {
        if (!hasMoreInput()) {
            return -1;
        }
        return current.stream[current.cursor];
    }

    Mark mark() {
        return new Mark(current);
    }

    void reset(Mark mark) {
        current = new Mark(mark);
    }

    /**
     * search the stream for a match to a string
     * @param string The string to match
     * @return <strong>true</strong> is one is found, the current position
     *         in stream is positioned after the search string, <strong>
     *               false</strong> otherwise, position in stream unchanged.
     */
    boolean matches(String string) throws CasperException {
        Mark mark = mark();
        int ch = 0;
        int i = 0;
        do {
            ch = nextChar();
            if (((char) ch) != string.charAt(i++)) {
                reset(mark);
                return false;
            }
        } while (i < string.length());
        return true;
    }

    boolean matchesETag(String tagName) throws CasperException {
        Mark mark = mark();

        if (!matches("</" + tagName)) {
            return false;
        }
        skipSpaces();
        if (nextChar() == '>') {
            return true;
        }

        reset(mark);
        return false;
    }

    boolean matchesETagWithoutLessThan(String tagName)
            throws CasperException {
        Mark mark = mark();

        if (!matches("/" + tagName)) {
            return false;
        }
        skipSpaces();
        if (nextChar() == '>') {
            return true;
        }

        reset(mark);
        return false;
    }

    /**
     * Looks ahead to see if there are optional spaces followed by
     * the given String.  If so, true is returned and those spaces and
     * characters are skipped.  If not, false is returned and the
     * position is restored to where we were before.
     */
    boolean matchesOptionalSpacesFollowedBy(String s)
            throws CasperException {
        Mark mark = mark();

        skipSpaces();
        boolean result = matches(s);
        if (!result) {
            reset(mark);
        }

        return result;
    }

    int skipSpaces() throws CasperException {
        int i = 0;
        while (hasMoreInput() && isSpace()) {
            i++;
            nextChar();
        }
        return i;
    }

    /**
     * Skip until the given string is matched in the stream.
     * When returned, the context is positioned past the end of the match.
     *
     * @param s The String to match.
     * @return A non-null <code>Mark</code> instance (positioned immediately
     *         before the search string) if found, <strong>null</strong>
     *         otherwise.
     */
    Mark skipUntil(String limit) throws CasperException {
        Mark ret = null;
        int limlen = limit.length();
        int ch;

        skip:
        for (ret = mark(), ch = nextChar(); ch != -1;
                ret = mark(), ch = nextChar()) {
            if (ch == limit.charAt(0)) {
                Mark restart = mark();
                for (int i = 1; i < limlen; i++) {
                    if (peekChar() == limit.charAt(i)) {
                        nextChar();
                    } else {
                        reset(restart);
                        continue skip;
                    }
                }
                return ret;
            }
        }
        return null;
    }

    /**
     * Skip until the given string is matched in the stream, but ignoring
     * chars initially escaped by a '\'.
     * When returned, the context is positioned past the end of the match.
     *
     * @param s The String to match.
     * @return A non-null <code>Mark</code> instance (positioned immediately
     *         before the search string) if found, <strong>null</strong>
     *         otherwise.
     */
    Mark skipUntilIgnoreEsc(String limit) throws CasperException {
        Mark ret = null;
        int limlen = limit.length();
        int ch;
        int prev = 'x';        // Doesn't matter

        skip:
        for (ret = mark(), ch = nextChar(); ch != -1;
                ret = mark(), prev = ch, ch = nextChar()) {
            if (ch == '\\' && prev == '\\') {
                ch = 0;                // Double \ is not an escape char anymore
            } else if (ch == limit.charAt(0) && prev != '\\') {
                for (int i = 1; i < limlen; i++) {
                    if (peekChar() == limit.charAt(i)) {
                        nextChar();
                    } else {
                        continue skip;
                    }
                }
                return ret;
            }
        }
        return null;
    }

    /**
     * Skip until the given end tag is matched in the stream.
     * When returned, the context is positioned past the end of the tag.
     *
     * @param tag The name of the tag whose ETag (</tag>) to match.
     * @return A non-null <code>Mark</code> instance (positioned immediately
     *               before the ETag) if found, <strong>null</strong> otherwise.
     */
    Mark skipUntilETag(String tag) throws CasperException {
        Mark ret = skipUntil("</" + tag);
        if (ret != null) {
            skipSpaces();
            if (nextChar() != '>') {
                ret = null;
            }
        }
        return ret;
    }

    final boolean isSpace() throws CasperException {
        // Note: If this logic changes, also update Node.TemplateText.rtrim()
        return peekChar() <= ' ';
    }

    /**
     * Parse a space delimited token.
     * If quoted the token will consume all characters up to a matching quote,
     * otherwise, it consumes up to the first delimiter character.
     *
     * @param quoted If <strong>true</strong> accept quoted strings.
     */
    String parseToken(boolean quoted) throws CasperException {
        StringBuilder StringBuilder = new StringBuilder();
        skipSpaces();
        StringBuilder.setLength(0);

        if (!hasMoreInput()) {
            return "";
        }

        int ch = peekChar();

        if (quoted) {
            if (ch == '"' || ch == '\'') {

                char endQuote = ch == '"' ? '"' : '\'';
                // Consume the open quote:
                ch = nextChar();
                for (ch = nextChar(); ch != -1 && ch != endQuote;
                        ch = nextChar()) {
                    if (ch == '\\') {
                        ch = nextChar();
                    }
                    StringBuilder.append((char) ch);
                }
                // Check end of quote, skip closing quote:
                if (ch == -1) {
                    err.casperError(mark(), "casper.error.quotes.unterminated");
                }
            } else {
                err.casperError(mark(), "casper.error.attr.quoted");
            }
        } else {
            if (!isDelimiter()) {
                // Read value until delimiter is found:
                do {
                    ch = nextChar();
                    // Take care of the quoting here.
                    if (ch == '\\') {
                        if (peekChar() == '"' || peekChar() == '\''
                                || peekChar() == '>' || peekChar() == '%') {
                            ch = nextChar();
                        }
                    }
                    StringBuilder.append((char) ch);
                } while (!isDelimiter());
            }
        }

        return StringBuilder.toString();
    }

    /**
     * Parse utils - Is current character a token delimiter ?
     * Delimiters are currently defined to be =, &gt;, &lt;, ", and ' or any
     * any space character as defined by <code>isSpace</code>.
     *
     * @return A boolean.
     */
    private boolean isDelimiter() throws CasperException {
        if (!isSpace()) {
            int ch = peekChar();
            // Look for a single-char work delimiter:
            if (ch == '=' || ch == '>' || ch == '"' || ch == '\''
                    || ch == '/') {
                return true;
            }
            // Look for an end-of-comment or end-of-tag:
            if (ch == '-') {
                Mark mark = mark();
                if (((ch = nextChar()) == '>')
                        || ((ch == '-') && (nextChar() == '>'))) {
                    reset(mark);
                    return true;
                } else {
                    reset(mark);
                    return false;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
