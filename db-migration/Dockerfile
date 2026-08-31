FROM gradle:9.7.1-jdk25-alpine AS builder

WORKDIR /app

COPY . .
RUN gradle :db-migration:installDist --no-daemon -x test --stacktrace

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/db-migration/build/install/db-migration .

ENTRYPOINT ["./bin/db-migration"]
