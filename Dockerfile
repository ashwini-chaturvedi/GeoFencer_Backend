# Use Maven with Eclipse Temurin JDK
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /geo_trackerApp

# Copy the POM file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code & build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use Eclipse Temurin JDK for the runtime image
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory
WORKDIR /geo_trackerApp

# Copy the built JAR file from the build stage
COPY --from=build /geo_trackerApp/target/admin-0.0.1-SNAPSHOT.jar .

# Expose the port your Spring Boot app uses
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "admin-0.0.1-SNAPSHOT.jar"]