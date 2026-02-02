FROM eclipse-temurin:19-jdk AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -v

COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -e -DskipTests package

FROM eclipse-temurin:19-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
