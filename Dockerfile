# ---------------------------------------------
# Stage 1: Build the Java application with Maven
# ---------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory inside container
WORKDIR /app

# Copy Maven configuration first for better caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Download dependencies (caching step)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (change jar name if needed)
RUN mvn clean package -DskipTests

# ---------------------------------------------
# Stage 2: Run the built application
# ---------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS runner
  

WORKDIR /app

# Copy the built JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Set default environment (overridden by .env)


# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
