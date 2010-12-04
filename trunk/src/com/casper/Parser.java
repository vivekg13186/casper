package com.casper;

import java.io.CharArrayWriter;

/**
 * Parser - compile casper code to Node tree
 */
class Parser {

    private CasperReader reader;
    private Mark start;
    private ErrorDispatcher err;

    /**
     * The constructor
     */
    private Parser(CasperReader reader) {
        this.err = reader.getErrorDispatcher();
        this.reader = reader;
        start = reader.mark();
    }

    /**
     * The main entry for Parser
     */
    public static Node.Nodes parse(CasperReader reader)
            throws CasperException {

        Parser parser = new Parser(reader);

        Node.Root root = new Node.Root(reader.mark(), null);

        while (reader.hasMoreInput()) {
            parser.parseElements(root);
        }

        Node.Nodes page = new Node.Nodes(root);
        return page;
    }


    /*
    AllBody ::=
    ( '<%--' CasperCommentBody )
    | ( '<%=' ExpressionBody )
    | ( '${' ELExpressionBody )
    | ( '<%' ScriptletBody )
    | TemplateText
     */
    private void parseElements(Node parent) throws CasperException {
        start = reader.mark();
        if (reader.matches("<%--")) {
            parseComment(parent);
        } else if (reader.matches("<%=")) {
            parseExpression(parent);
        } else if (reader.matches("<%")) {
            parseScriptlet(parent);
        } else if (reader.matches("${")) {
            parseELExpression(parent, '$');
        } else {
            checkUnbalancedEndTag();
            parseTemplateText(parent);
        }
    }

    /*
     * CasperCommentBody ::= (Char* - (Char* '--%>')) '--%>'
     */
    private void parseComment(Node parent) throws CasperException {
        start = reader.mark();
        Mark stop = reader.skipUntil("--%>");
        if (stop == null) {
            err.casperError(start, "casper.error.unterminated", "&lt;%--");
        }

        new Node.Comment(reader.getText(start, stop), start, parent);
    }


    /*
     * ExpressionBody ::= (Char* - (char* '%>')) '%>'
     */
    private void parseExpression(Node parent) throws CasperException {
        start = reader.mark();
        Mark stop = reader.skipUntil("%>");
        if (stop == null) {
            err.casperError(start, "casper.error.unterminated", "&lt;%=");
        }

        new Node.Expression(parseScriptText(reader.getText(start, stop)),
                start, parent);
    }

    /*
     * ScriptletBody ::= (Char* - (char* '%>')) '%>'
     */
    private void parseScriptlet(Node parent) throws CasperException {
        start = reader.mark();
        Mark stop = reader.skipUntil("%>");
        if (stop == null) {
            err.casperError(start, "casper.error.unterminated", "&lt;%");
        }

        new Node.Scriptlet(parseScriptText(reader.getText(start, stop)), start,
                parent);
    }


    /*
     * ELExpressionBody (following "${" to first unquoted "}") // XXX add formal
     * production and confirm implementation against it, // once it's decided
     */
    private void parseELExpression(Node parent, char type)
            throws CasperException {
        start = reader.mark();
        Mark last = null;
        boolean singleQuoted = false, doubleQuoted = false;
        int currentChar;
        do {
            last = reader.mark();
            currentChar = reader.nextChar();
            if (currentChar == '\\' && (singleQuoted || doubleQuoted)) {
                // skip character following '\' within quotes
                reader.nextChar();
                currentChar = reader.nextChar();
            }
            if (currentChar == -1) {
                err.casperError(start, "casper.error.unterminated", type + "{");
            }
            if (currentChar == '"' && !singleQuoted) {
                doubleQuoted = !doubleQuoted;
            }
            if (currentChar == '\'' && !doubleQuoted) {
                singleQuoted = !singleQuoted;
            }
        } while (currentChar != '}' || (singleQuoted || doubleQuoted));

        new Node.ELExpression(reader.getText(start, last), start, parent);
    }

    /*
     * Flag as error if an unbalanced end tag appears by itself.
     */
    private void checkUnbalancedEndTag() throws CasperException {

        if (!reader.matches("</")) {
            return;
        }

        err.casperError(start, "casper.error.unbalanced.endtag");
    }

    /*
     * Parse for a template text string until '<' or "${" or "#{" is encountered,
     * recognizing escape sequences "\%", "\$", and "\#".
     */
    private void parseTemplateText(Node parent) throws CasperException {

        if (!reader.hasMoreInput()) {
            return;
        }

        CharArrayWriter ttext = new CharArrayWriter();
        // Output the first character
        int ch = reader.nextChar();
        if (ch == '\\') {
            reader.pushChar();
        } else {
            ttext.write(ch);
        }

        while (reader.hasMoreInput()) {
            ch = reader.nextChar();
            if (ch == '<') {
                reader.pushChar();
                break;
            } else if ((ch == '$' || ch == '#')) {
                if (!reader.hasMoreInput()) {
                    ttext.write(ch);
                    break;
                }
                if (reader.nextChar() == '{') {
                    reader.pushChar();
                    reader.pushChar();
                    break;
                }
                ttext.write(ch);
                reader.pushChar();
                continue;
            } else if (ch == '\\') {
                if (!reader.hasMoreInput()) {
                    ttext.write('\\');
                    break;
                }
                char next = (char) reader.peekChar();
                // Looking for \% or \$ or \#
                if (next == '%' || (next == '$' || next == '#')) {
                    ch = reader.nextChar();
                }
            }
            ttext.write(ch);
        }

        new Node.TemplateText(ttext.toString(), start, parent);
    }

    private String parseScriptText(String tx) {
        CharArrayWriter cw = new CharArrayWriter();
        int size = tx.length();
        int i = 0;
        while (i < size) {
            char ch = tx.charAt(i);
            if (i + 2 < size && ch == '%' && tx.charAt(i + 1) == '\\'
                    && tx.charAt(i + 2) == '>') {
                cw.write('%');
                cw.write('>');
                i += 3;
            } else {
                cw.write(ch);
                ++i;
            }
        }
        cw.close();
        return cw.toString();
    }
}
