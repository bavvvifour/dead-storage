FROM maven:3.9.4 AS build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
