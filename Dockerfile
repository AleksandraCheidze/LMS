FROM docker.io/gradle:7.5.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --stacktrace

FROM docker.io/alpine:3.16.3

RUN apk update && apk --no-cache add openjdk11
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk/
EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/lms-be-0.0.1-SNAPSHOT.jar /app/lms-be.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/lms-be.jar"]
