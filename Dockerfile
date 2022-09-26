FROM gradle:jdk17-alpine AS gradle
WORKDIR /app
COPY . .
RUN gradle bootJar

FROM openjdk:17 as runtime
WORKDIR /app

ENV PORT 8080
ENV SPRING_PROFILE "prod"
ENV JPA_DDL "none"
ENV SHOW_SQL "false"
ENV INIT_SQL "always"

COPY --from=gradle /app/build/libs/*.jar /app/app.jar
RUN chown -R 1000:1000 /app
USER 1000:1000

ENTRYPOINT ["java", "-jar", "app.jar"]