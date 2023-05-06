FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY target/gateway-0.0.1-SNAPSHOT.jar artbridge-gateway.jar
ENTRYPOINT ["java","-jar","/artbridge-gateway.jar"]
