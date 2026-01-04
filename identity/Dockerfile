# ===== STAGE 1: Build the application =====
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build the application
RUN ./mvnw -q -e -DskipTests clean package

# ===== STAGE 2: Create lightweight runtime image =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the JAR from stage 1
COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
