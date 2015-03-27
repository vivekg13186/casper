## About Casper ##
Scriptlet language for java.Provide JSP style code translation supports expression(<%=),scriptlet(<%) and expression(${) tags like JSPs.
Javascript used as the core scripting language.

## Quick Start ##

### Using console ###
  1. Download casper.jar from the download page.
  1. Create a new text file _'hello.casper'_ and copy the following code.
```
<%= "Hello World" %>
${ "Hello World" }
<% print("Hello World") %>
```
  1. Type the following command in console **java -jar casper.jar hello.casper**
  1. You will see "Hello World" 3 times in console

Read more

  1. [User Guide](http://code.google.com/p/casper/wiki/UserGuide)
  1. [Developer Guide](http://code.google.com/p/casper/wiki/DeveloperGuide)
  1. [Web User Guide](http://code.google.com/p/casper/wiki/WebDeveloper)