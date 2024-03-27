FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/bookstore-api-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["java", "-jar", "bookstore-api-0.0.1-SNAPSHOT.jar"]