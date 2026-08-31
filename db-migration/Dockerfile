FROM gradle:9-jdk21-alpine AS builder

WORKDIR /app

COPY . .
RUN gradle :db-migration:installDist --no-daemon -x test --stacktrace

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/db-migration/build/install/db-migration .

ENTRYPOINT ["./bin/db-migration"]
