FROM gradle:jdk11-hotspot AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon


FROM adoptopenjdk/openjdk11
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/pauta-service.jar
ENTRYPOINT ["java", "-jar", "/app/pauta-service.jar"]