# Build stage: We use a maven image with Java 17 to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the JAR file skipping tests to speed up deployment natively
RUN mvn clean package -DskipTests

# Run stage: A lightweight JRE image to only run the built JAR file
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=build /app/target/*.jar app.jar

# Spring Boot defaults to port 8080
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]
