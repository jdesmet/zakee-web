# zakee-web

I wanted to custom build a close-to JEE 7 Compliant Application Server, using a 
minimalistic approach. My aim was also to use as much as possible the reference 
implemenations and avoid any customizations if at all possible.

Because of those goals, You should be able to do a plain vanilla install of Tomcat,
and add in additional JEE support by simply using the right combination of Maven
Dependencies. There is even a Tomcat Maven Depencency that can download, install,
and deploy to a Tomcat instance with a single command.

BTW, I kind of wanted to name this project something, so I thought ... "well, Tomcat
is not really a cat's name, so maybe I can find something more suitable."
As a kid I had a cat. His name was Zakie, and in thoughts similar of Tomee (J-EE), I 
tought I could name this project 'Zakee' (which is pronounced the same as the
Dutch Zakie anyhow).

# Create your own

to create your own web application, you can use this as a template. Just start dropping
in your htmlx and java files. You might also consider configuring the webapp to run,
including:
- Adding additional libraries
- Configuring JSF extensions like IceFaces etc
- modifying the context path, which is set in src/main/webapp/META-INF/context.xml and in
the pom.xml.

# Start Tomcat

The pom is preconfigured to work with [Cargo Maven Plugin](https://codehaus-cargo.github.io/cargo/Maven2+plugin.html). 
to start and deploy the project on tomcat 8 from the command-line:

```
    mvn clean package cargo:run
```

With the pre-configured settings, it will use servlet port :9090, and ajp port :9009. Once
started, you should be able to access the main webpage form [http://localhost:9090/zakee-web/].

Note that this project will just as well work with NetBeans, for which the cargo
settings will be ignored. With NetBeans, you can just 'run' the project with a 
preconfigured Application Server (Tomcat 8).

The project also runs in Eclipse without any changes.


# Docker

This can be easily dockerized as well - a sample Dockerfile has been included. A version has been pushed to Docker Hub as well, although I cannot guarantee which version. To find it in Docker Hub: jdesmet/zakee-web:latest

```
    docker pull jdesmet/zakee-web:latest
    docker run -it -p 8080:8080 jdesmet/zakee-web
```

