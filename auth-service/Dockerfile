# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17-alpine as build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker cache
RUN mvn dependency:go-offline

COPY src/ /app/src/
COPY .mvn/ /app/.mvn/
COPY mvnw /app/mvnw
COPY mvnw.cmd /app/mvnw.cmd
RUN chmod +x ./mvnw && mvn clean package -DskipTests

# Stage 2: Run with JRE only
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8088
ENV USER_SERVICE_URL=http://user-service:8086

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
