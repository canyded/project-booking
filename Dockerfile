# Use a Debian-based JDK image for the builder stage
FROM openjdk:17-slim AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
ENV JAVA_OPTS="-Xms256m -Xmx512m"

RUN chmod +x ./gradlew

# Install findutils (provides xargs) using apt-get
RUN apt-get update && apt-get install -y findutils



# Build the application
RUN ./gradlew build -x test

# Create final image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
