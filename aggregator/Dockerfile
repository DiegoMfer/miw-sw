# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17-alpine as build
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker cache
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn clean package 

# Stage 2: Run with JRE only
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
