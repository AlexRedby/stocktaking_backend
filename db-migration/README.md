# Database tooling

## Migration application

The `db-migration` application brings the PostgreSQL schema up to date with
Liquibase. It loads the shared typed database configuration, opens a JDBC
connection, and applies pending migrations.

Run it from the repository root after PostgreSQL is available. The
`runWithDotEnv` task reads database settings from the root `.env` file; see the
[configuration contract](../docs/configuration.md) for supported values.

Linux/macOS:

```sh
./gradlew :db-migration:runWithDotEnv
```

Windows:

```bat
gradlew.bat :db-migration:runWithDotEnv
```

## jOOQ code generation

jOOQ generation is a development tool separate from migrating a deployed
database. It starts a temporary PostgreSQL container, applies the current
Liquibase changelog, and generates Kotlin models from that schema. Docker must
be available.

Run it from the repository root:

```sh
./gradlew :db-migration:jooqCodegen
```
