# Use an official JDK image as base
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Package the application
RUN ./mvnw clean package -DskipTests

# Run the application
# Use shell form so * expands to the actual JAR file
CMD java -jar target/*.jar
