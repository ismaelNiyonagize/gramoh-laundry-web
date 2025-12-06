# ===== Build Stage =====
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

# ===== Run Stage =====
FROM eclipse-temurin:17-jdk
WORKDIR /app

#COPY --from=build /app/target/*.jar app.jar

COPY --from=build /app/target/gramoh-laundry-web-*.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
