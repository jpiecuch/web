FROM openjdk:8
WORKDIR /app
COPY target/web-*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]