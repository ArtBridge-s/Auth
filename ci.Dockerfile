# Use the official maven/Java image as the base image
FROM maven:3.8.1-adoptopenjdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy the src, pom.xml, and sonar-project.properties files into the working directory
COPY src ./src
COPY pom.xml .
COPY sonar-project.properties .

# Install Node.js
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_14.x | bash - && \
    apt-get install -y nodejs

# Copy the package*.json files into the working directory
COPY package*.json ./

# Install npm dependencies
RUN npm ci

# Build the project
RUN mvn -X package

# Use the official openjdk image as the base image for the runtime environment
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar /app/

# Run the application
ENTRYPOINT ["java","-jar","/app/gateway-0.0.1-SNAPSHOT.jar"]
