FROM openjdk:16-jdk-slim
# WORKDIR /app
COPY ./vikabot /app
EXPOSE 8080
# ENTRYPOINT ["java" "-jar" "/app/vikabot-1.0-SNAPSHOT-all.jar"]
ENTRYPOINT ["/app/bin/vikabot"]

# FROM openjdk:16-jdk-alpine AS build
# WORKDIR /app
# COPY ./ /app
# RUN ./gradlew build --no-daemon --stacktrace
#
# FROM openjdk:16-jdk-slim
# EXPOSE 8080
# RUN mkdir /home/app
# COPY --from=build /app/build/install/vikabot /home/app
# WORKDIR /home/app
# ENTRYPOINT ["bin/vikabot"]


# FROM gradle:7.4.2-jdk18-alpine AS build
# COPY --chown=gradle:gradle . /home/gradle/src
# WORKDIR /home/gradle/src
# RUN gradle build --no-daemon --stacktrace

# FROM openjdk:16-jdk-slim
# EXPOSE 8080
# RUN mkdir /home/app
# COPY --from=build /home/gradle/src/build/install /app
# ENTRYPOINT ["/app/vikabot/bin/vikabot"]