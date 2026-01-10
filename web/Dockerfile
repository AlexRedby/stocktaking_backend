FROM gradle:9-jdk21-alpine AS deps

WORKDIR /app

RUN mkdir -p /gradle_cache
ENV GRADLE_USER_HOME=/gradle_cache

# Create empty directories for each modules of root gradle file
RUN mkdir -p clients/tarkov-dev-apollo \
    clients/tarkov-dev-kobby \
    db-migration \
    model

COPY gradle.properties *.gradle.kts ./
COPY gradle ./gradle
COPY buildSrc ./buildSrc

# Modules used by web
COPY clients/tarkov-dev-apollo/*.gradle.kts ./clients/tarkov-dev-apollo/
COPY clients/tarkov-dev-apollo/src/main/graphql ./clients/tarkov-dev-apollo/src/main/graphql
COPY model/*.gradle.kts ./model/

COPY web/*.gradle.kts ./web/

RUN gradle :web:build --no-daemon -x test --stacktrace

FROM gradle:9-jdk21-alpine AS builder

WORKDIR /app

COPY --from=deps /gradle_cache /gradle_cache
ENV GRADLE_USER_HOME=/gradle_cache

COPY . .
RUN gradle :web:installDist --no-daemon -x test --stacktrace

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/web/build/install/* .
RUN chmod +x bin/web

EXPOSE 8080

ENTRYPOINT ["./bin/web"]
