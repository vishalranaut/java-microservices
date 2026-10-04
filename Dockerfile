# Build any module:  docker build --build-arg MODULE=user-service -t user-service .
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
ARG MODULE
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre
ARG MODULE
WORKDIR /app
COPY --from=build /build/${MODULE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
