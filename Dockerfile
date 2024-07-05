#build the app
FROM maven:3.9.7-sapmachine-22 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DSkipTests
RUN echo "done"

#run the app
FROM openjdk:22-slim
ADD target/eventastic-0.0.1-SNAPSHOT.jar eventastic-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "eventastic-0.0.1-SNAPSHOT.jar"]