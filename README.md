# Configuration

Local database settings are read from the ignored root `.env` file by the
documented Gradle run tasks. Web startup and migrations use the same typed
database configuration contract. See
[docs/configuration.md](docs/configuration.md) for the supported variables,
defaults, precedence, and failure behavior.

## .env file

Copy `.env.example` to `.env` in the repository root and replace the password
placeholder. Docker Compose reads the same file automatically. For local Gradle
launches, use `:web:runWithDotEnv`; direct IDE launches must provide the same
variables in the run configuration.

# Run app

## Docker

```sh
docker-compose up --build -d
```

## Local

Linux/Mac:

```sh
./gradlew :web:runWithDotEnv
```

Windows:

```bash
gradlew.bat :web:runWithDotEnv
```

## Database migrations

See the [db-migration README](db-migration/README.md) for the module description
and run commands.

## HTTP API

See the [HTTP API contract](docs/api.md) for supported requests, responses, and
errors.

# Development

## Tarkov.dev Apollo schema and generated sources

The checked-in schema is
`clients/tarkov-dev-apollo/src/main/graphql/tarkov-dev.graphqls`. Refresh it
from the configured Tarkov.dev introspection endpoint before reviewing an API
contract change:

```sh
./gradlew :clients:tarkov-dev-apollo:downloadTarkovdevApolloSchemaFromIntrospection
```

Generate the Kotlin operation models after changing the schema or GraphQL
operations:

```sh
./gradlew :clients:tarkov-dev-apollo:generateTarkovdevApolloSources
```

On Windows use `gradlew.bat` with the same task names. Generated Kotlin sources
live under the module's `build/` directory and must not be edited or committed.

To check possible dependencies upgrade run next command and update in `gradle/libs.versions.toml` manually...

Linux/Mac:
```sh
./gradlew dependencyUpdates
```

Windows:

```bash
gradlew.bat dependencyUpdates
```

To upgrade gradle wrapper version run...

Linux/Mac:
```sh
./gradlew wrapper --gradle-version latest
```

Windows:

```bash
gradlew.bat wrapper --gradle-version latest
```
