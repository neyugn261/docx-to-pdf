FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk21
WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

COPY --from=build /app/target/docx-to-pdf-1.0-SNAPSHOT.war webapps/ROOT.war
COPY --from=build /app/src/main/resources/database/init.sql /docker-entrypoint-initdb.d/

RUN apt-get update && apt-get install -y mysql-client && rm -rf /var/lib/apt/lists/*

COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8386

ENTRYPOINT ["/docker-entrypoint.sh"]
