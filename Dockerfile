FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY pom.xml serenity.properties ./
COPY src ./src
COPY scripts ./scripts

ENTRYPOINT ["sh", "/app/scripts/run-tests.sh"]
