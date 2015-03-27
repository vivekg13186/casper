# User Guide #

## Introduction ##
**Casper** is a simple scriplet language written in java .Uses JSP like coding conversion.

## Script Tag Basics ##

_**Scriptlet**_ - **<%** _javascript code_ **%>**
> All javascript action statement like for,if,while can be embedded using scriptlet tag.

_**Expression**_ - **<%=** _valid javascript expression_ **%>**
> All template text to be printed can be embedded using expression tag.Eg. <%= variable %>

_**Expression Language or EL tag**_ - **${** _valid javascript expression_ **}**
> All template text to be printed can be embedded using expression tag.Eg. ${ variable }

## Table of Content ##
| Title | Interface| About |
|:------|:---------|:------|
| [Hello World](UserGuide#Example_1_-_Hello_World_Template.md) | Console | small scriptlet to explain how tags works |
| [Output File Redirection](UserGuide#Example_2_-_Redirecting_output_to_file.md) | Console | explain how to redirect output to file |
| [Javascript Code](UserGuide#Example_3_-_Compile_scriptlet_to_javascript.md) | Console | explain how to get intermediate javascript code |
| [Embedding Casper](UserGuide#Example_4_-_Embedding_in_java.md) | Java | explain how to embedded in java code |
| [Handling Variables](UserGuide#Example_5_-_Using_java_variables_in_scriptlet.md) | Java | explain how to use java object within scriptlet code |
| [File IO](UserGuide#Example_6_-_Reading_and_writing_to_file_using_Casper_java_API.md) | Java | explain how to read scriplet code from file and write output to a file |
| [$out()](UserGuide#Example_7_-_Understanding_print()_and_$out()_functions.md) | Java | explain the difference between $out() and print() function |
| [Compiled Javascript code](UserGuide#Example_8_-_Get_Compiled_java_script_code_using_java.md)| Java | explain how to get intermediate javascript code using java api |


## Example 1 - Hello World Template ##
> _Example to demonstrate how to use the tag and print 'Hello World'_
  1. Create a file **'ex.casper'** and add the following code to the file
```
       <%=  "Hello World using expression tag" %>
       ${  "Hello World using EL tag" }
       <% print("Hello World using scriptlet tag") %>
```
  1. Execute the program - _**java -jar casper.jar ex.casper**_
  1. Output :
```
        Hello World using expression tag
        Hello World using EL tag
        Hello World using scriptlet tag
```


---


## Example 2 - Redirecting output to file ##
> _Example to demonstrate how to write the result to output file_
  1. Create a file **'ex.casper'** and add the following code to the file
```
       <%=  "Hello World using expression tag" %>
       ${  "Hello World using EL tag" }
       <% print("Hello World using scriptlet tag") %>
```
  1. Execute the program - _**java -jar casper.jar ex.casper result.text.**_
  1. Open **'result.text'** file you can see the following output :
```
        Hello World using expression tag
        Hello World using EL tag
        Hello World using scriptlet tag
```


---


## Example 3 - Compile scriptlet to javascript ##
> _Casper convert scriplet to an intermediate javascript code and execute it using the javascript engine.This example demonstrate how to get the intermediate javascript code._
  1. Create a file **'ex.casper'** and add the following code to the file.
```
       <%=  "Hello World using expression tag" %>
       ${  "Hello World using EL tag" }
       <% print("Hello World using scriptlet tag") %>
```
  1. Execute the program - _**java -jar ex.jar -compile hello.casper result.text.**_
  1. Open **'result.text'** file you can see the following output :
```
        function $out(input){
              if(input==undefined || input==null || input=="")
                return
              casper.print(input)
        }
        $out(" ");
        $out(("Hello World using expression tag"));
        $out("\r\n       ");
        $out(("Hello World using EL tag"));
        $out("\r\n       ");
        print("Hello World using scriptlet tag") 
```
> > _If you don't provide result.text in command output will printed in your console_


> _$out() is a casper defined function use to print output to the corresponding outputstream more.._


---


## Example 4 - Embedding in java ##
> _Example to demonstrate how to embedded capser in your java code_
  1. Create a file **'Main.java'** and add the following code to the file
```
       import com.casper.Casper;
       import com.casper.CasperException;
       import java.io.ByteArrayInputStream;
       import java.io.IOException;
       import javax.script.ScriptException;

       public class Main {

          public static void main(String[] args) throws CasperException, IOException, ScriptException {
        
             String code ="<%= \"Hello World\" %>";
            //Don't forget to add casper.jar to your java class library
           Casper.eval(new ByteArrayInputStream(code.getBytes()),System.out);
          }

      }
```
  1. Compile your Main.java code and run.
  1. Output
```
        Hello World
```
> > _Casper class(API class) provide static methods to eval and compile you code._
      1. eval'('InputStream in,OutputStream op) - takes scriplet code and write result in op.
      1. eval'('InputStream in,OutputStream op,Properties var) - var property used to define java variable to your scriptlet.
      1. compile'('InputStream in,OutputStream op) - takes scriplet code and write javascript code.


---


## Example 5 - Using java variables in scriptlet ##

> _Example to demonstrate how to pass java objects to scriplet.In this example we will pass name a input from java code and print it scriptlet_
  1. Create a file **'Main.java'** and add the following code to the file
```
     import com.casper.Casper;
     import com.casper.CasperException;
     import java.io.ByteArrayInputStream;
     import java.io.IOException;
     import java.util.Properties;
     import javax.script.ScriptException;

     public class Main {

           public static void main(String[] args) throws CasperException, IOException, ScriptException {
        
               String code ="Hello ${name} !";
               Properties var  = new Properties();
               var.put("name","vivek");
               //Don't forget to add casper.jar to your java class library
               //Don't forget to add var result in script exception
               Casper.eval(new ByteArrayInputStream(code.getBytes()),System.out,var);
           }
     }
```
  1. Compile your Main.java code and run
  1. Output
```
        Hello vivek !
```


---


## Example 6 - Reading and writing to file using Casper java API ##
> _Example to demonstrate how to read scriplet from a file and eval and write the output to a file.In this example we use hello.casper as input and result.text as output file._
  1. Create a file **'hello.casper'** and add the following code to the file.
```
      Hello <% if(name =="vivek" ) { %> handsome <% } else { %> beautiful  <% } %> ${name}
```
  1. Create a file **'Main.java'** and add the following code to the file
```
     import com.casper.Casper;
     import com.casper.CasperException; 
     import java.io.FileInputStream;
     import java.io.FileOutputStream;
     import java.io.IOException;
     import java.util.Properties;
     import javax.script.ScriptException;
    
     public class Main {

          public static void main(String[] args) throws CasperException, IOException, ScriptException {
        
              Properties var  = new Properties();
              var.put("name","vivek");
              //Don't forget to add casper.jar to your java class library
             //Don't forget to add var result in script exception
             //Make sure the file paths are correct
            Casper.eval(new FileInputStream("hello.casper"),new FileOutputStream("result.text"),var);
              }

    }

```
  1. Compile your Main.java code and run
  1. Output in result.text
```
        Hello handsome vivek 
```
> > _Here concept of using java object inside script is also introduced.Javascript will check for 'name' object if it is 'vivek' it will return handsome ,otherwise it will return beautiful._


---


## Example 7 - Understanding print() and $out() functions ##

> _Example to demonstrate the difference between **'print'** and **'out'** functions._

  1. Create a file **'hello.casper'** and add the following code to the file.
```
      <% print("This line printed by print function"); %>
      <% $out("This line printed by $out function"); %>
```
  1. Create a file **'Main.java'** and add the following code to the file
```
     import com.casper.Casper;
     import com.casper.CasperException; 
     import java.io.FileInputStream;
     import java.io.FileOutputStream;
     import java.io.IOException;
     import java.util.Properties;
     import javax.script.ScriptException;
    
     public class Main {

          public static void main(String[] args) throws CasperException, IOException, ScriptException {
        
              //Don't forget to add casper.jar to your java class library
             //Don't forget to add var result in script exception
             //Make sure the file paths are correct
            Casper.eval(new FileInputStream("hello.casper"),new FileOutputStream("result.text"),var);
              }

    }

```
  1. Compile your Main.java code and run
  1. Output in result.text
```
        
       This line printed by $out function 
```
  1. Output in console
```
           This line printed by print function
```
> > _$out() is used by casper to write output to outputstream define in the function.if it is defined as System.out the it will print result in console.But print() function is default javascript function will print result in console.If you try to execute hello.casper using casper.jar(ref Example 1) you can see but the line in console.Avoid using print() function unless your are clear with the execution._


---


## Example 8 - Get Compiled java script code using java ##

> _Example to demonstrate how to get intermediate javascript code using java class._

  1. Create a file **'hello.casper'** and add the following code to the file.
```
       <%= "say hello " %>  
```
  1. Create a file **'Main.java'** and add the following code to the file
```
       import com.casper.Casper;
       import com.casper.CasperException;
       import java.io.FileInputStream;
       import java.io.FileOutputStream;
       import java.io.IOException;
       import javax.script.ScriptException;

       public class Main {

           public static void main(String[] args) throws CasperException, IOException, ScriptException {


           //Don't forget to add casper.jar to your java class library
          //Don't forget to add var result in script exception
          //Make sure the file path are correct
          Casper.compile(new FileInputStream("hello.casper"),new FileOutputStream("result.text"));
          }

      }

```
  1. Compile your Main.java code and run
  1. Output in result.text
```
        
      function $out(input){
          if(input==undefined || input==null || input=="")
            return
          casper.print(input)
      }
      $out(("say hello"));
 
```
> > _compile() deliver the intermediate javascript code to the output stream._