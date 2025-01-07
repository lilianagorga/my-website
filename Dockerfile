FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
RUN apk add --no-cache maven
COPY pom.xml .
COPY src ./src
RUN find ./src -type f \( -name "*.properties" ! -name "application-prod.properties" -o -name "*.env*" \) -delete
RUN mvn clean package -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/my-website-0.0.1-SNAPSHOT.jar"]