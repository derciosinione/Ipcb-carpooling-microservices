# ===== STAGE 1: Build the application =====
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml first to cache dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Pre-download dependencies (this layer is cached until pom.xml changes)
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -v

# Copy source and build
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -e -DskipTests package

# ===== STAGE 2: Create lightweight runtime image =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the JAR from stage 1
COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
