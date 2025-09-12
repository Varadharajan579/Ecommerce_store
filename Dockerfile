# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (to leverage Docker cache)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy full source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-jammy AS run
WORKDIR /app

# Copy only the final fat JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (default Spring Boot = 8080)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
