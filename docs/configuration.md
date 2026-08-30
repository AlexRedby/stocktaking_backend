# Database configuration contract

The web service and database migration application use the same database
configuration contract.

## Variables

| Variable | Type | Required | Default |
| --- | --- | --- | --- |
| `DB_HOST` | String | No | `localhost` |
| `DB_PORT` | Integer | No | `5432` |
| `DB_NAME` | String | No | `stocktaking` |
| `DB_USER` | String | No | `stocktaking` |
| `DB_PASSWORD` | String | Yes | None |

Environment variables override the defaults. `DB_PASSWORD` must always be
provided and has no fallback value.

## Supplying configuration

For local Gradle execution, the following tasks load variables from the
repository-root `.env` file:

- `:web:runWithDotEnv`;
- `:db-migration:runWithDotEnv`.

Docker Compose reads the same `.env` file. Direct process launches and other
deployment systems must provide the variables through their environment.

## Validation

Configuration is validated before the application uses the database. Missing
required values and values with incompatible types, such as a non-integer
`DB_PORT`, stop startup instead of falling back silently.
