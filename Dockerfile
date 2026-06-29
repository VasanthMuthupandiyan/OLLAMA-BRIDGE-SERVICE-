# Stage 1: Build the application using a standard Maven image
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Copy project configuration
COPY pom.xml ./

# Pre-download dependencies to cache this layer
RUN mvn dependency:go-offline -B

# Copy the source code and compile the package (skipping test execution during build)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run stage with a minimal JRE image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Add a non-root system user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy only the compiled JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default 7483 port configured in our .env
EXPOSE 7483

# Set container entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
