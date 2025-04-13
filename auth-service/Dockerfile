FROM maven:3.9-eclipse-temurin-17-alpine as build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
# Add --debug flag to see more details
RUN mvn clean package -DskipTests -e

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
