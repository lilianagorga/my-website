FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN apk add --no-cache maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ls -l
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package
EXPOSE 8080
CMD ["java", "-jar", "target/my-website-0.0.1-SNAPSHOT.jar"]