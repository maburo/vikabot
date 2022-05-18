# FROM openjdk:16-jdk-slim
# # WORKDIR /app
# COPY ./vikabot /app
# EXPOSE 8080
# # ENTRYPOINT ["java" "-jar" "/app/vikabot-1.0-SNAPSHOT-all.jar"]
# ENTRYPOINT ["/app/bin/vikabot"]

# FROM openjdk:16-jdk-alpine AS build
# WORKDIR /app
# COPY ./ /app
# RUN ./gradlew build --no-daemon --stacktrace
#
# FROM openjdk:16-jdk-slim
# EXPOSE 8080
# RUN mkdir /home/app
# COPY --from=build /build/libs/vikabot-1.0-SNAPSHOT.jar /home/app/vikabot.jar
# WORKDIR /home/app
# ENTRYPOINT ["java", "-jar", "vikabot"]


FROM gradle:7.4.2-jdk18-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --stacktrace

FROM openjdk:16-jdk-slim
EXPOSE 8080
RUN mkdir /home/app
COPY --from=build /home/gradle/src/build/libs/vikabot-1.0-SNAPSHOT.jar /app/vikabot.jar
ENTRYPOINT ["java", "-jar", "vikabot.jar"]