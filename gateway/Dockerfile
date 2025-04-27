FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy maven executable and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the maven wrapper executable
RUN chmod +x mvnw

# Download all dependencies
# This is done in a separate step to leverage Docker cache
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create a non-root user to run the application
RUN addgroup --system --gid 1001 appgroup \
    && adduser --system --uid 1001 --gid 1001 appuser

# Copy JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Switch to the non-root user
USER appuser

# Environment variables that can be overridden
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV SERVICE_USER_URL=http://user-service:8086
ENV SERVICE_SEARCH_URL=http://search-service:8084
ENV SERVICE_HISTORY_URL=http://history-service:8085

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
