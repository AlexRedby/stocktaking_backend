# TODO

- Test if apollo better to use compare to kobby
- Add documentation how to run schema download and code generation

# Secrets

Secrets are not stored in git and should be manually added to environment variables before app launch.
The list is next:

| Name        | Description          |
|-------------|----------------------|
| DB_PASSWORD | Password of database |

There is several ways how to store this environment variables.

## .env file

Copy `.env.example` to `.env` file in root of repository. Fill it with needed values, where each variable on a new line.
With docker this will work without additional steps,
but for local launch need to add `.env` file in configuration of the IDE or run Gradle task `:web:runWithDotEnv`.

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

# Development

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
