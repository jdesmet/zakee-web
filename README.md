# zakee-web

I wanted to custom build a close-to JEE 7 Compliant Application Server, using a 
minimalistic approach. My aim was also to use as much as possible the reference 
implemenations and avoid any customizations if at all possible.

Because of those goals, You should be able to do a plain vanilla install of Tomcat,
and add in additional JEE support by simply using the right combination of Maven
Dependencies. There is even a Tomcat Maven Depencency that can download, install,
and deploy to a Tomcat instance with a single command.

BTW, I kind of wanted to name this project something, so I thought ... "well, Tomcat
is not really a cat's name, but as a kid I had a cat. His name was Zakie, and in the
light of Tomee, tought I could name this project 'Zakee' (which is pronounced the same as the
Dutch Zakie anyhow).

Base Tomcat basically is just a Servlet Container with support for Servlets and 
JSP. No included JEE Features like CDI, JSF. 

To understand what comes shipped with the base Tomcat installation, you can use
following table:

| JEE Spec | Tomcat 7.0.x | Tomcat 8.0.x | Tomcat 9.0.x |
|----------------|---------------|---------------|-----|
|Servlet | 3.0 ([JSR315](http://www.jcp.org/en/jsr/summary?id=315)) | 3.1 ([JSR340](http://www.jcp.org/en/jsr/detail?id=340))| 4.0 |
|JSP | 2.2 ([JSR245](http://www.jcp.org/en/jsr/summary?id=245)) | 2.3 | 2.4? |
|EL (Expression Language) | 2.2 ([JSR245](http://www.jcp.org/en/jsr/summary?id=245)) | 3.0 ([JSR341](http://www.jcp.org/en/jsr/summary?id=341)) | 3.1 ? |
|WebSocket | 1.1 ([JSR356](http://www.jcp.org/en/jsr/summary?id=356)) | 1.1 ([JSR346](http://www.jcp.org/en/jsr/summary?id=356)) | 1.2 ? |
|Common Annotations | 1.1 (jsr250-api) | 1.2 (annotation-api) |

More information can be found on

* [Tomcat Specifications](http://wiki.apache.org/tomcat/Specifications)
* [Tomcat Versions](http://wiki.apache.org/tomcat/TomcatVersions)
* [Maven Dependencies for JEE Specification](http://mvnrepository.com/open-source/java-specs)

As this sits on top Git, I will not attempt to create different setups depending on the Tomcat
version. You want an earlier version, we can go about using Git to pull and rebuild, but this document
as of current writing, will focus exclusively on Tomcat 8.

Now in addition to the Base Tomcat 8.0.x install base, we will add in following JEE support:

| JEE Spec | Specification (API) | Implementation (SPI) |
|----------|---------------------|----------------------|
| CDI | 1.2 | Weld 2.3.2 |
| JSF | 2.2 | GlassFish Mojarra 2.2.12 |
| Bean Validation | 1.1.0 ([JSR303](http://www.jcp.org/en/jsr/summary?id=303)) | Hibernate Validator 5.2.3 |
| JAX-RS | 2.0.1 (javax.ws.rs-api) | Jersey 2.22.1 (JDK7 or later) |

This means that the most notable absence is JPA and JTA. Also at this point integration between CDI and JAX-RS
is somewhat broken, as enabling integration between the two causes the JAX-RS injectin annotations to be broken,
although plain CDI will work.

## Choosing the Implementations (dependencies)

Because Tomcat Doesn’t come with CDI, JSF, or Jax-RS baked into it, we will have to choose 
ourselves which implementation we are going to include. Well, kind of like that, 
never could agree with others, and I have the chance to choose latest versions
independent of support offered by the vendor of the server.

However we are not completely on our own in choosing the implementation. We still 
need to validate that the implementations will work with Tomcat. That also means 
choosing which version of Tomcat to use.

## Tomcat

As of writing Tomcat 8 is out, and includes support for the Servlet API 3.1 
Specification ([JSR-340](https://jcp.org/aboutJava/communityprocess/final/jsr340/)). 
This is what I am going to employ as my servlet container. I could have chosen 
Jetty as well.

Below shows the Maven dependency you can use to be able to compile your code. We note 
that the actual implementation libraries come shipped with Tomcat, so we will tag this 
dependency with a “provided” scope. This will make sure that the libraries will not 
be packaged as part of the war, and allow us to use the server-side libraries.

```xml
<dependency>
  <!-- Provided by Tomcat, but needed for compilation -->
  <groupId>javax</groupId>
  <artifactId>javaee-web-api</artifactId>
  <version>7.0</version>
  <scope>provided</scope>
</dependency>
```

Note however that with this javaee-web-api inclusion we included probably too much. 
To avoid errors after deploying, you might narrow down the compile time libraries a 
little better for the libraries that will be available server side.

## CDI

The latest specification is CDI 1.2 ([JSR-346](https://jcp.org/aboutJava/communityprocess/mrel/jsr346/index.html)). 
Because the Reference Implementation used is that of Weld, I thought it to be 
convenient to use Weld as well. A quick check in the [Weld Documentation](https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html#_tomcat) confirms 
that there is support for Tomcat 7 and Tomcat 8.

To be able to compile against the specification, we only need to include a dependency for the API.

```xml
<dependency>
  <groupId>javax.enterprise</groupId>
  <artifactId>cdi-api</artifactId>
  <version>1.2</version>
  <scope>provided</scope>
</dependency>
```

Note that these libraries are just compile time libraries, and are supplied using more specific vendor implementations. Because these API’s will not be used at run-time, we supplied the “provided” scope. This will make it only available during compile phase,and will not be deployed as part of the war.

But now because we do not have any server side implementation chosen yet, we still need to somehow include the Weld libraries. These Weld libraries are just the implementation of the dependency container, and are not required for compilation.

```xml
<dependency>
  <!--
    JBoss/Weld Refrence Implementation for
    CDI on a Servlet Container
  -->
  <groupId>org.jboss.weld.servlet</groupId>
  <artifactId>weld-servlet</artifactId>
  <version>2.2.6.Final</version>
  <scope>runtime</scope>
</dependency>
```

Because we do not need it at the compile phase, as we compiled against the more lightweight API, we tag the scope as “runtime”, making it only included as part of the war deployment, but not as a compilation target. If it were a fully Java EE 7 compliant server, we would simply not have included this dependency.

## JSF

The story for Java Server Faces (JSF) goes a similar route. The latest specification is JSF 2.2 ([JSR-344](https://jcp.org/aboutJava/communityprocess/final/jsr344/)). The reference implementation is [Oracle’s Mojarra](https://javaserverfaces.java.net/). JSF 2.2 is also the standard for Java EE 7 ([JSR-342](http://jcp.org/en/jsr/detail?id=342)). So choices made.

To compile against the API use below Maven dependency.

```xml
<dependency>
  <groupId>javax.faces</groupId>
  <artifactId>javax.faces-api</artifactId>
  <version>2.2</version>
  <scope>provided</scope>
</dependency>
```

Again we set it to “provided” to avoid distributing the API on the server as part of the war. Now to be able to run it, we still need to include the libraries in the war for server side deployment.

```xml
<dependency>
  <!-- This is the Mojarra Implementation of JSF -->
  <groupId>org.glassfish</groupId>
  <artifactId>javax.faces</artifactId>
  <version>2.2.8-02</version>
  <scope>runtime</scope>
</dependency>
```

Again, for a fully Java EE 7 compliant server, we would simply not have included this dependency.

With these dependencies added to your web project, you might be able to compile everything, but you won’t be able to do much yet, and might see exceptions. We still need to configure things further to enable CDI and JSF.

## JAX-RS

## Configuration

The following is the list of configuration files that require attention:

* WEB-INF/web.xml
* WEB-INF/beans.xml
* META-INF/context.xml

WEB-INF/web.xml — add in the JSF Servlet (Faces Servlet), and bootstrap Weld

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app
  xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
  version="3.1"
  >
  <session-config>
    <session-timeout>30</session-timeout>
  </session-config>
  <resource-env-ref>
    <!-- Enable Weld CDI, also needs META-INF/context.xml entry -->
    <resource-env-ref-name>BeanManager</resource-env-ref-name>
    <resource-env-ref-type>javax.enterprise.inject.spi.BeanManager</resource-env-ref-type>
  </resource-env-ref>
  <context-param>
    <param-name>javax.faces.PROJECT_STAGE</param-name>
    <param-value>Development</param-value>
  </context-param>
  <servlet>
    <servlet-name>Faces Servlet</servlet-name>
    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>Faces Servlet</servlet-name>
    <url-pattern>/faces/*</url-pattern>
  </servlet-mapping>
  <welcome-file-list>
    <welcome-file>faces/index.xhtml</welcome-file>
  </welcome-file-list>
</web-app>
```

WEB-INF/beans.xml, to enable CDI (the existence of an empty file is usually enough)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
  xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
  bean-discovery-mode="annotated"
  >
</beans>
```

META-INF/context.xml, to setup the Weld BeanManager as a Resource at startup as JNDI cannot be modified at runtime for self-registry.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context antiJARLocking="true" path="/tomcat-test">
  <Resource
    name="BeanManager"
    auth="Container"
    type="javax.enterprise.inject.spi.BeanManager"
    factory="org.jboss.weld.resources.ManagerObjectFactory"
    />
</Context>
```

With these things added in you should be able to fire up your server and deploy. Still might not be able to do much without some actual code and webpages added.

## Sample WebApp

With just two files, we can showcase CDI and JSF.

index.xhtml (src/main/webapp)

```xml
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:h="http://xmlns.jcp.org/jsf/html"
  >
  <h:head>
    <title>Facelet Title</title>
  </h:head>
  <h:body>
    Hello from ${me.name}
  </h:body>
</html>
```

Me.java (src/main/java)

```java
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
 
@Named
@RequestScoped
public class Me implements Serializable {
  private String name;
 
  public String getName() {
    return name;
  }
 
  public void setName(String name) {
    this.name = name;
  }
 
  @PostConstruct
  public void init() {
    name = "Jo Desmet";
  }
}
```




To compile against the API use below Maven dependency.
