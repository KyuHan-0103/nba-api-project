# Step 1: Build the app using Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the app using just Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/player-compare-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]