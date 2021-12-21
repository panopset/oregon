export JAVA_HOME="/usr/java/oraclejdk/jdk1.8.0_112"
export PATH=PATH=${JAVA_HOME}/bin:${PATH}
mvn clean install
mvn spring-boot:run

