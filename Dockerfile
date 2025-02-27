# Step 1: Build the application with Maven
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Create the final image using a smaller JRE
FROM openjdk:17-jdk-slim
WORKDIR /app

# Correct the JAR file path, copying the exact JAR from the build stage
COPY --from=build /app/target/Document_Tracking_and_E_Clearance-0.0.1-SNAPSHOT.jar /app/Document_Tracking_and_E_Clearance-0.0.1-SNAPSHOT.jar

# Expose the application's port
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/Document_Tracking_and_E_Clearance-0.0.1-SNAPSHOT.jar"]