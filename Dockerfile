FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y rabbitmq-server

WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY start.sh start.sh

EXPOSE 8000

ENTRYPOINT ["/bin/sh", "start.sh"]