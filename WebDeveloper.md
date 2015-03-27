# Introduction #

This guide walk through how to configure casper in your web application.


# Installation #

  * Download latest version of "casper.jar" file from download page.
  * Copy "casper.jar" to your web application folder by default it will be _**"webappfolder/WEB-INF/lib/"**_.
  * Open "webappfolder/WEB-INF/**web.xml**" file and add the following code snippet under 

&lt;web-app&gt;

**tag
```
        <servlet>
           <description>eval casper files</description>
           <servlet-name>casper</servlet-name>
           <servlet-class>com.casper.CasperDispatcher</servlet-class>
        <init-param>
           <param-name>casper</param-name>
          <param-value>$dir-path$/</param-value>
        </init-param>
       </servlet>
       <servlet-mapping>
       <servlet-name>casper</servlet-name>
        <url-pattern>*.casper</url-pattern>
    </servlet-mapping>
    
```
  * Replace**$dir-path$**with you dir folder path where put all your `*`.casper files.**

> Thats it now you have successfully installed casper into your web application.
## What is the code snippet and how it work ? ##
  * First we configure our casper Servlet which comes as a part of casper.jar file ,also we are providing a addtional paramater $dir-path$ this tell servlet where to pick up the casper files.Note make sure you are adding '/' at end of file path otherwise casper will through exception.
```
       <servlet>
           <description>eval casper files</description>
           <servlet-name>casper</servlet-name>
           <servlet-class>com.casper.CasperDispatcher</servlet-class>
        <init-param>
           <param-name>casper</param-name>
          <param-value>$dir-path$/</param-value>
        </init-param>
       </servlet>
   
```
  * Now we redirect all casper request to casper servlet which we configured above.
```
           <servlet-mapping>
            <servlet-name>casper</servlet-name>
            <url-pattern>*.casper</url-pattern>
       </servlet-mapping>
```


# Example #

  * Create a file "hello.casper" in the $dir-path$ defined in web.xml.
  * Add the following code snippet to "hello.casper" file.
```
        <% var name = request.getParameter("name"); %>
        <%= "Hello" %>
        <% if(name!=null) { %>
                    ${name}
        <% }  %>
```
  * Type the URL in your browser "http://hostname:port/appname/hello.casper?name=vivek"
  * You will get the following result
```
      Hello vivek
```
> > _You can direct use request object in your casper file._


_Soon will upgrade with ant file for installation._