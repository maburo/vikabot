FROM gradle:7.4.2-jdk18-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --stacktrace

# FROM openjdk:16-jdk-alpine AS build
# WORKDIR /app
# COPY ./ /app
# RUN ./gradlew build --no-daemon --stacktrace


FROM openjdk:16-jdk-slim
EXPOSE 8080
RUN mkdir /app

COPY --from=build /home/gradle/src/build/install /app
ENTRYPOINT ["/app/vikabot/bin/vikabot"]
# ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]

# COPY --from=build /app/build/install/vikabot /app
# ENTRYPOINT ["/app/bin/vikabot"]