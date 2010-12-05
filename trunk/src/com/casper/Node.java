package com.casper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * Node Tree
 */
abstract class Node {


    protected Nodes body;

    protected String text;

    protected Mark startMark;

    protected int beginJavaLine;

    protected int endJavaLine;

    protected Node parent;

    private boolean isDummy;

    /**
     * Zero-arg Constructor.
     */
    public Node() {
        this.isDummy = true;
    }

    
    public Node(Mark start, Node parent) {
        this.startMark = start;
        this.isDummy = (start == null);
        addToParent(parent);
    }

    public Node(String text,Mark start, Node parent) {
        this.text=text;
        this.startMark = start;
        this.isDummy = (start == null);
        addToParent(parent);
    }

    public Nodes getBody() {
        return body;
    }

    public void setBody(Nodes body) {
        this.body = body;
    }

    public String getText() {
        return text;
    }

    public Mark getStart() {
        return startMark;
    }

    public Node getParent() {
        return parent;
    }

    public int getBeginJavaLine() {
        return beginJavaLine;
    }

    public void setBeginJavaLine(int begin) {
        beginJavaLine = begin;
    }

    public int getEndJavaLine() {
        return endJavaLine;
    }

    public void setEndJavaLine(int end) {
        endJavaLine = end;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public Node.Root getRoot() {
        Node n = this;
        while (!(n instanceof Node.Root)) {
            n = n.getParent();
        }
        return (Node.Root) n;
    }


    /**
     * Selects and invokes a method in the visitor class based on the node type.
     * This is abstract and should be overrode by the extending classes.
     *
     * @param v
     *            The visitor class
     */
    abstract void accept(Visitor v) throws CasperException;

    // *********************************************************************
    // Private utility methods

    /*
     * Adds this Node to the body of the given parent.
     */
    private void addToParent(Node parent) {
        if (parent != null) {
            this.parent = parent;
            Nodes parentBody = parent.getBody();
            if (parentBody == null) {
                parentBody = new Nodes();
                parent.setBody(parentBody);
            }
            parentBody.add(this);
        }
    }



    public static class Nodes {

        private List<Node> list;

        private Node.Root root; // null if this is not a page

        private boolean generatedInBuffer;

        public Nodes() {
            list = new Vector<Node>();
        }

        public Nodes(Node.Root root) {
            this.root = root;
            list = new Vector<Node>();
            list.add(root);
        }

        /**
         * Appends a node to the list
         *
         * @param n
         *            The node to add
         */
        public void add(Node n) {
            list.add(n);
            root = null;
        }

        /**
         * Removes the given node from the list.
         *
         * @param n
         *            The node to be removed
         */
        public void remove(Node n) {
            list.remove(n);
        }

        /**
         * Visit the nodes in the list with the supplied visitor
         *
         * @param v
         *            The visitor used
         */
        public void visit(Visitor v) throws CasperException, IOException {
            Iterator<Node> iter = list.iterator();
            while (iter.hasNext()) {
                Node n = iter.next();
                n.accept(v);
            }
        }

        public int size() {
            return list.size();
        }

        public Node getNode(int index) {
            Node n = null;
            try {
                n = list.get(index);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            return n;
        }

        public Node.Root getRoot() {
            return root;
        }

        public boolean isGeneratedInBuffer() {
            return generatedInBuffer;
        }

        public void setGeneratedInBuffer(boolean g) {
            generatedInBuffer = g;
        }
    }


     /**
     * Represents the root of the code
     */
    public static class Root extends Node {

        private Root parentRoot;

        /*
         * Constructor.
         */
        Root(Mark start, Node parent) {
            super(start, parent);

            // Figure out and set the parent root
            Node r = parent;
            while ((r != null) && !(r instanceof Node.Root))
                r = r.getParent();
            parentRoot = (Node.Root) r;
        }

        @Override
        public void accept(Visitor v) throws CasperException {
            try {
                v.visit(this);
            } catch (IOException ex) {
                throw new CasperException("kill", ex);
            }
        }

        public Root getParentRoot() {
            return parentRoot;
        }

    }

       
    public static class Comment extends Node {

        public Comment(String text, Mark start, Node parent) {
            super(text, start, parent);
        }

        @Override
        public void accept(Visitor v) throws CasperException {
            try {
                v.visit(this);
            } catch (IOException ex) {
                throw new CasperException("casper.visitor.comment", ex);
            }
        }
    }


     /**
     * Represents an expression, declaration, or scriptlet
     */
    public static abstract class ScriptingElement extends Node {

        public ScriptingElement(String text,
                Mark start, Node parent) {
            super(text, start, parent);
        }

        public ScriptingElement( Mark start, Node parent) {
            super(null,start, parent);
        }

        @Override
        public String getText() {
            String ret = text;
            if (ret == null) {
                if (body != null) {
                    StringBuilder buf = new StringBuilder();
                    for (int i = 0; i < body.size(); i++) {
                        buf.append(body.getNode(i).getText());
                    }
                    ret = buf.toString();
                } else {
                    ret = "";
                }
            }
            return ret;
        }

        @Override
        public Mark getStart() {
            if (text == null && body != null && body.size() > 0) {
                return body.getNode(0).getStart();
            } else {
                return super.getStart();
            }
        }
    }

      /**
     * Represents an expression. Expressions in attributes are embedded in the
     * attribute string and not here.
     */
    public static class Expression extends ScriptingElement {

        public Expression(String text, Mark start, Node parent) {
            super(text, start, parent);
        }

        public Expression( Mark start, Node parent) {
            super(start, parent);
        }

        @Override
        public void accept(Visitor v) throws CasperException {
            try {
                v.visit(this);
            } catch (IOException ex) {
                throw new CasperException("casper.visitor.expression", ex);
            }
        }
    }

    /**
     * Represents a scriptlet
     */
    public static class Scriptlet extends ScriptingElement {

        public Scriptlet(String text, Mark start, Node parent) {
            super(text, start, parent);
        }

        public Scriptlet(Mark start, Node parent) {
            super(start, parent);
        }

        @Override
        public void accept(Visitor v) throws CasperException {
            try {
                v.visit(this);
            } catch (IOException ex) {
                throw new CasperException("casper.visitor.scriplet", ex);
            }
        }
    }

    /**
     * Represents an EL expression. Expressions in attributes are embedded in
     * the attribute string and not here.
     */
    public static class ELExpression extends Node {

       
        public ELExpression(String text, Mark start, Node parent) {
            super(text, start, parent);
            }

        @Override
        public void accept(Visitor v) throws CasperException {
            try {
                v.visit(this);
            } catch (IOException ex) {
                throw new CasperException("casper.visitor.expressionlang", ex);
            }
        }

    }

        /**
     * Represents a template text string
     */
    public static class TemplateText extends Node {

        private ArrayList<Integer> extraSmap = null;

        public TemplateText(String text, Mark start, Node parent) {
            super(text, start, parent);
        }

        @Override
        public void accept(Visitor v) throws CasperException {
            try {
                v.visit(this);
            } catch (IOException ex) {
                throw new CasperException("casper.visitor.templatetext", ex);
            }
        }

        /**
         * Trim all whitespace from the left of the template text
         */
        public void ltrim() {
            int index = 0;
            while ((index < text.length()) && (text.charAt(index) <= ' ')) {
                index++;
            }
            text = text.substring(index);
        }

        public void setText(String text) {
            this.text = text;
        }

        /**
         * Trim all whitespace from the right of the template text
         */
        public void rtrim() {
            int index = text.length();
            while ((index > 0) && (text.charAt(index - 1) <= ' ')) {
                index--;
            }
            text = text.substring(0, index);
        }

        /**
         * Returns true if this template text contains whitespace only.
         */
        public boolean isAllSpace() {
            boolean isAllSpace = true;
            for (int i = 0; i < text.length(); i++) {
                if (!Character.isWhitespace(text.charAt(i))) {
                    isAllSpace = false;
                    break;
                }
            }
            return isAllSpace;
        }

        /**
         * Add a source to Java line mapping
         *
         * @param srcLine
         *            The position of the source line, relative to the line at
         *            the start of this node. The corresponding java line is
         *            assumed to be consecutive, i.e. one more than the last.
         */
        public void addSmap(int srcLine) {
            if (extraSmap == null) {
                extraSmap = new ArrayList<Integer>();
            }
            extraSmap.add(new Integer(srcLine));
        }

        public ArrayList<Integer> getExtraSmap() {
            return extraSmap;
        }
    }


        /**
     * A visitor class for visiting the node. This class also provides the
     * default action (i.e. nop) for each of the child class of the Node. An
     * actual visitor should extend this class and supply the visit method for
     * the nodes that it cares.
     */
    public static class Visitor {

        Writer out;
        public Visitor(OutputStream out){
            this.out=new OutputStreamWriter(out);
        }
        public Visitor(){
            this.out=new StringWriter();//TODO weird code to be handled
        }
        

        /**
         * Visit the body of a node, using the current visitor
         */
        protected void visitBody(Node n) throws CasperException, IOException {
            if (n.getBody() != null) {
                n.getBody().visit(this);
            }

        }

        public void visit(Root n) throws CasperException, IOException {
            //function to print eval text 
            //to avoid undefined and null in output text
            out.write("function $out(input){\n"+
                      "if(input==undefined || input==null || input==\"\")\n" +
                      "return\n"+
                      "casper.print(input)\n"+
                      "}\n");
            visitBody(n);
            
        }
        public static void generate(Nodes nodes,OutputStream op) throws CasperException, IOException{
            Visitor v =new Visitor(op);
            nodes.visit(v);
            v.out.close();
        }
        public void visit(Comment n) throws CasperException, IOException {
            //can be enabled if needed
            //out.write("/*"+n.text+"*/\n");
       }

        //removed eval function
        public void visit(Expression n) throws CasperException, IOException {
            
            if(n.text!=null)
            out.write("$out(("+n.text+"));\n");
        }

        public void visit(Scriptlet n) throws CasperException, IOException {
            if(n.text!=null)
            out.write(n.text);
        }

        //removed eval function
        public void visit(ELExpression n) throws CasperException, IOException {
            if(n.text!=null)
            out.write("$out(("+n.text+"));\n");
        }

        public void visit(TemplateText n) throws CasperException, IOException {
            if(n.text!=null)
            out.write("$out((\""+escapeJavaScriptString(n.text)+"\"));\n");
        }

        //Supporting functions for code generation
        //convert code string to javascript string
        public static String escapeJavaScriptString(String string) {
        if (string == null) {
            return null;
        }

        StringBuffer escaped = new StringBuffer();
        int sz;
        sz = string.length();
        for (int i = 0; i < sz; i++) {
            char ch = string.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                escaped.append("\\u" + hex(ch));
            } else if (ch > 0xff) {
                escaped.append("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                escaped.append("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        escaped.append('\\');
                        escaped.append('b');
                        break;
                    case '\n':
                        escaped.append('\\');
                        escaped.append('n');
                        break;
                    case '\t':
                        escaped.append('\\');
                        escaped.append('t');
                        break;
                    case '\f':
                        escaped.append('\\');
                        escaped.append('f');
                        break;
                    case '\r':
                        escaped.append('\\');
                        escaped.append('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            escaped.append("\\u00" + hex(ch));
                        } else {
                            escaped.append("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        escaped.append('\\');
                        escaped.append('\'');
                        break;
                    case '"':
                        escaped.append('\\');
                        escaped.append('"');
                        break;
                    case '\\':
                        escaped.append('\\');
                        escaped.append('\\');
                        break;
                    case '/':

                        escaped.append('\\');

                        escaped.append('/');
                        break;
                    default:
                        escaped.append(ch);
                        break;
                }
            }
        }
        return escaped.toString();
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    }




}