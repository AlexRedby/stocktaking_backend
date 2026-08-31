FROM gradle:9.7.1-jdk25-alpine AS builder

WORKDIR /app

COPY . .
RUN gradle \
    :configuration:test \
    :clients:tarkov-dev-apollo:test \
    :model:test \
    :web:test \
    :web:installDist \
    --no-daemon \
    --stacktrace

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/web/build/install/* .
RUN chmod +x bin/web

ENV APP_HOST=0.0.0.0 \
    APP_PORT=8080 \
    APP_DEVELOPMENT=false

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=2m --retries=5 \
    CMD wget -q -O /dev/null "http://127.0.0.1:${APP_PORT}/health/ready" || exit 1

ENTRYPOINT ["./bin/web"]
