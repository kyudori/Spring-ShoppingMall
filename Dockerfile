FROM amazoncorretto:17

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/shoppingmall-0.0.1-SNAPSHOT.jar"]
