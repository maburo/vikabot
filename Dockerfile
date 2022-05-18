# FROM gradle:7.4.2-jdk18-alpine AS build
# COPY --chown=gradle:gradle . /home/gradle/src
# WORKDIR /home/gradle/src
# RUN gradle build --no-daemon --stacktrace

FROM openjdk:16-jdk-alpine AS build
WORKDIR /app
COPY ./ /app
RUN ./gradlew build --no-daemon --stacktrace


FROM openjdk:16-jdk-slim
EXPOSE 8080
RUN mkdir /home/app

# COPY --from=build /home/gradle/src/build/install /app
# ENTRYPOINT ["/app/vikabot/bin/vikabot"]

COPY --from=build /app/build/install/vikabot /home/app
WORKDIR /home/app
ENTRYPOINT ["bin/vikabot"]