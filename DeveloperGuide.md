# Developer Guide #

## Casper API ##
> _All functionality provided by casper defined in **com.casper.Casper** class_

```
   public class Casper {

    /*
     *Simple wrapper for Main eval function.
     */
    public static void eval(InputStream io, OutputStream op) throws CasperException, IOException, ScriptException;

    /*
     * Main eval function
     * Compile and Eval capser code and return the final output.
     * variables - holds java defined object for jsengine ,can be set to null if not required
     */

    public static void eval(InputStream io, OutputStream op, Properties variables) throws CasperException, IOException, ScriptException;

    /*
     * Compiler
     * Convert capser code to javascript code abd write it to the output stream
     */
    public static void compile(InputStream io, OutputStream op) throws CasperException, IOException; 


   }

```

  * _**eval**_ - Method compile scriptlet code into javascript code and evaluate the javascript code and return the result.
  * _**InputStream io**_ - Scriptlet code input.
  * _**OutputStream op**_ - Final result text output.
  * _**Properties var**_ - Allow java object asscess to scriptlet ref [UserGuide](UserGuide#Example_5_-_Using_java_variables_in_scriptlet.md)

## Console ##
> Console funtionality is implemented in com.casper.Console class.

> ### Usage ###

  1. inputfilename                            ;return evaluated output
  1. _**-compile**_ inputfilename                   ;return javascript code
  1. inputfilename outputfilename             ;return evaluated output to outputfile
  1. _**-compile**_ inputfilename outputfilename    ;return javascript code to outputfile

_use **-compile** flag to get javascript source code_