# Stocktaking backend

The backend loads crafting and barter data from Tarkov.dev, builds the crafting
graph, and exposes the HTTP API used for item autocomplete, crafting trees, and
tool names. The Ktor application owns the API and graph cache. Database schema
changes are applied by a separate migration application, not by the web runtime.

## Run

Create the repository-root `.env` file from `.env.example`. The supported
variables, defaults, and validation rules are defined in the
[configuration contract](docs/configuration.md).

### Docker Compose

```sh
docker-compose up --build -d
```

Compose starts PostgreSQL, applies migrations, and then starts the web service.
The service is ready when `GET http://localhost:8080/health/ready` returns
`200 OK`.

### Local Gradle processes

Run PostgreSQL and apply migrations using the
[database tooling instructions](db-migration/README.md). Then start the web
service:

```sh
./gradlew :web:runWithDotEnv
```

On Windows, use `gradlew.bat` with the same task names.

## Database tooling

The [database tooling instructions](db-migration/README.md) describe schema
migrations and jOOQ code generation.

## HTTP API

The [HTTP API contract](docs/api.md) defines the supported endpoints, graph
payload, health checks, and error responses.

## Development

### Verification

With Docker available, run the complete build, including the Testcontainers
migration test:

```sh
./gradlew build --no-daemon
docker-compose build
```

### Tarkov.dev schema and generated sources

Refresh the checked-in Tarkov.dev GraphQL schema before reviewing an upstream
contract change:

```sh
./gradlew :clients:tarkov-dev-apollo:downloadTarkovdevApolloSchemaFromIntrospection
```

After changing the schema or GraphQL operations, regenerate the Apollo models:

```sh
./gradlew :clients:tarkov-dev-apollo:generateTarkovdevApolloSources
```

Generated Apollo sources live under the module's `build` directory and are not
committed.

### Dependency and Gradle updates

```sh
./gradlew dependencyUpdates
./gradlew wrapper --gradle-version latest
```

Review reported dependency updates before changing `gradle/libs.versions.toml`.
