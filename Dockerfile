# Use a lightweight OpenJDK image
# Use the latest Maven and JDK images to minimize vulnerabilities
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY . .
RUN apk update && apk upgrade --available && mvn clean package -DskipTests

FROM eclipse-temurin:21.0.3_9-jre-alpine
WORKDIR /app
# Update packages in the runtime image as well
RUN apk update && apk upgrade --available
# Copy the built jar from the builder stage (update the jar name if needed)
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
