# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src
# Build the jar file, skipping tests to speed up the docker build
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]