# Use a base image with Java and Maven to build the application
FROM maven:3.8.4-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Use a lightweight base image with Java to run the application
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /app
COPY --from=build /app/target/Cart-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
