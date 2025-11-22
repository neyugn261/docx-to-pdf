FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk21

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        mysql-client \
        fonts-dejavu \
        fonts-liberation \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf /usr/local/tomcat/webapps/*

WORKDIR /usr/local/tomcat

COPY --from=build /app/target/docx-to-pdf-1.0-SNAPSHOT.war webapps/ROOT.war
COPY --from=build /app/src/main/resources/database/init.sql /docker-entrypoint-initdb.d/

COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8386

ENTRYPOINT ["/docker-entrypoint.sh"]
