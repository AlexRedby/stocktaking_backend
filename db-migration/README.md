# db-migration

## Database migrations

The `db-migration` application brings the PostgreSQL schema up to date with
Liquibase. It loads the shared typed database configuration, opens a JDBC
connection, and applies `db/changelog-master.yaml`.

Run it from the repository root after PostgreSQL is available. The
`runWithDotEnv` task supplies database settings from the root `.env` file; see
[docs/configuration.md](../docs/configuration.md) for the configuration contract.

### Run

Linux/macOS:

```sh
./gradlew :db-migration:runWithDotEnv
```

Windows:

```bat
gradlew.bat :db-migration:runWithDotEnv
```

## jOOQ code generation

For Javassist reflection to work, run `JooqCodegenMain` with this JVM option:

```
--add-opens java.base/java.lang=ALL-UNNAMED
```
