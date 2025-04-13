# Use a lightweight OpenJDK image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and project files
COPY . .

# Grant permission to gradlew
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Expose the port used by the Spring Boot app
EXPOSE 8080

# Run the application
CMD ["./gradlew", "bootRun"]
