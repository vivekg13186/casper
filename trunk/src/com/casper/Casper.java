package com.casper;

import com.casper.Node.Nodes;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import javax.script.*;

/**
 * API for casper
 * provide static methods
 * eval - to eval casper language
 * compile - to compile casper to javascript code
 */
public class Casper {

    /*
     *Simple wrapper for Main eval function.
     */
    public static void eval(InputStream io, OutputStream op) throws CasperException, IOException, ScriptException {
        eval(io, op, null);
    }
    /*
     * Main eval function
     * Compile and Eval capser code and return the final output.
     * variables - holds java defined object for jsengine ,can be set to null if not required
     */

    public static void eval(InputStream io, OutputStream op, Properties variables) throws CasperException, IOException, ScriptException {

        // created new casper reader using default error handler
        CasperReader reader = new CasperReader(new InputStreamReader(io), new ErrorDispatcher());

        //parse input stream to node tree
        Nodes nodes = Parser.parse(reader);

        //piped streams - used to stream compiled javascript code js engine without interm string conversion
        PipedOutputStream pop = new PipedOutputStream();
        PipedInputStream pip = new PipedInputStream(pop);

        //generate javascript code from the node tree
        Node.Visitor.generate(nodes, pop);

        //Setting up javscript engine
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

        //check for java script engine
        if (jsEngine == null) {
            return;//silent escape
        }
        //object used by javascript $out function to write the output
        jsEngine.put("casper", new PrintStream(op));

        //set all varibles to js engine
        //all varibles are global
        if (variables != null) {
            Enumeration names = variables.propertyNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                //avoid overriding casper defined js elements
                if (!name.equals("casper") && !name.equals("$out")) {
                    jsEngine.put(name, variables.get(name));
                }
            }
        }

        //eval javascript code
        jsEngine.eval(new InputStreamReader(pip));
        
    }

    /*
     * Compiler
     * Convert capser code to javascript code abd write it to the output stream
     */
    public static void compile(InputStream io, OutputStream op) throws CasperException, IOException {

        // created new casper reader using default error handler
        CasperReader reader = new CasperReader(new InputStreamReader(io), new ErrorDispatcher());

        //parse input stream to node tree
        Nodes nodes = Parser.parse(reader);

        //generate javascript code from the node tree
        Node.Visitor.generate(nodes, op);
    }
}
