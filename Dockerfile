FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
RUN apk add --no-cache maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src ./src
COPY .env.production ./.env.production
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "target/my-website-0.0.1-SNAPSHOT.jar"]