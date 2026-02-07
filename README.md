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

Create `.env` file in root of repository. Fill it with needed values just like that, where each variable on a new line:

```
DB_PASSWORD=password
```

With docker this will work without additional steps,
but for local launch need to add `.env` file in configuration of the IDEA.

# Run app

To run app in docker:

```sh
docker-compose up --build -d
```

# Development

To check possible dependencies upgrade run next command and update in `gradle/libs.versions.toml` manually

Linux/Mac:
```sh
./gradlew dependencyUpdates
```

Windows:

```bat
gradlew.bat dependencyUpdates
```

To upgrade gradle wrapper version run

Linux/Mac:
```sh
./gradlew wrapper --gradle-version latest
```

Windows:

```bat
gradlew.bat wrapper --gradle-version latest
```
