# syntax=docker/dockerfile:experimental
FROM openjdk:17.0.2-jdk-slim-buster as build
WORKDIR /workspace/app

COPY build/libs /workspace/app/build/libs

RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*", "com.sysco.perso.analytics.PersoAnalyticsApp"]
