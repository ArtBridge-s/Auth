FROM adoptopenjdk:11-jdk AS build

WORKDIR /app

COPY src ./src
COPY pom.xml .
COPY sonar-project.properties .

RUN apt-get update && \
    apt-get install -y curl && \
    apt-get install -y maven && \
    curl -fsSL https://deb.nodesource.com/setup_14.x | bash - && \
    apt-get install -y nodejs

COPY package*.json ./

RUN npm ci

RUN mvn package

FROM adoptopenjdk:11-jre

WORKDIR /app

COPY --from=build /app/target/*.jar /app/

ENTRYPOINT ["java","-jar","/app/gateway-0.0.1-SNAPSHOT.jar"]
