FROM openjdk:17.0.2

WORKDIR /app

COPY ./target/CarRegistry-0.0.1-SNAPSHOT.jar .

ENV PORT 8000
EXPOSE $PORT

CMD ["java", "-jar", "CarRegistry-0.0.1-SNAPSHOT.jar"]

