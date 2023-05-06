FROM maven:3.8.1-adoptopenjdk-11 AS build
WORKDIR /app
COPY src ./src
COPY pom.xml .
RUN mvn package

FROM openjdk:17-jdk-alpine

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/

ENTRYPOINT ["java","-jar","/app/artbridge-gateway.jar"]
