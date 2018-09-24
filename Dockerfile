FROM openjdk:8
WORKDIR /app
RUN apt-get update && \
    curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-6.4.1-amd64.deb && \
    dpkg -i filebeat-6.4.1-amd64.deb
COPY target/web-*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]