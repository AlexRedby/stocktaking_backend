# TODO

Priorities: P1 blocks safe or correct use; P2 is required for reliability; P3 is cleanup or optimization.

## P1 - API correctness

- Define and enforce request/error contracts for `/api/crafting-tree` and `/api/craftable-items`. Make `target_item_id` required, return 400 for missing or malformed input and 404 for an unknown item, and map failures to a stable JSON error payload instead of `!!`/uncaught exceptions. Escape search text with `Regex.escape` or replace the regex implementation so characters such as `[`, `(`, `*`, quotes, and Unicode cannot produce a 500. Done when route tests cover success, missing ID, unknown ID, empty filter, punctuation, and Unicode.
- Preserve recipe identity and quantities in the graph contract. Edge IDs must be unique across alternative crafts/stations, and the DTO must carry recipe ID, output count, required item count, and explicit grouping of alternative recipes. Deduplicate station/handle entries by stable identity. Done when a fixture with two recipes sharing the same source and component round-trips without duplicate React Flow IDs or lost quantities.
- Decide the exact semantics of `/api/craftable-items`: either return only items with at least one craft or rename/document the endpoint as an all-item search. Done when fixtures containing leaf components and tools prove the selected behavior.

## P2 - Runtime reliability

- Handle Tarkov.dev failures explicitly. Inspect Apollo GraphQL errors, distinguish valid empty data from upstream failure, configure endpoint/timeouts/retries, and return a stable 502/503 contract. Done when mocked tests cover timeout, transport error, GraphQL partial/error response, and valid empty data.
- Make graph cache initialization single-flight and thread-safe, keep the mutable cache private, and define refresh/TTL behavior so upstream data does not remain stale for the process lifetime. Done when concurrent first requests perform one upstream load and a deterministic test verifies refresh and failure behavior.
- Resolve database lifecycle ownership. Either remove the unused PostgreSQL/jOOQ path from the web service or run Liquibase before the application becomes ready. Make migration resource lookup independent of the current working directory. Done when a fresh Docker volume receives the schema automatically and a database-backed smoke test passes, or when all unused DB infrastructure is removed.
- Add production configuration and health checks. Production must run with Ktor development mode disabled; server, database, and Tarkov.dev settings must be externally configurable; readiness must include required dependencies. Done when the production container exposes passing liveness/readiness checks and no production-only setting is hardcoded.
- Make the container/build inputs reproducible: pin the Gradle builder image to an exact supported version and run tests in the image/CI instead of using `-x test`. Done when a clean container build uses the wrapper-compatible version and executes the complete verification suite.

## P2 - Tests and contract gates

- Add unit tests for phrase search, graph traversal with cycles/diamonds, loop removal, duplicate recipes, recipe quantities, station handles, and craftable-item filtering.
- Add Ktor route tests for all supported API success and error responses.
- Add mocked Apollo client tests and a PostgreSQL/Testcontainers migration test.
- Add a cross-repository smoke test for autocomplete and crafting-tree rendering through the supported production proxy/origin topology. Done when the backend test/check task and the shared smoke test run in CI from a clean checkout.

## P2 - Dependency and toolchain upgrades

- Upgrade the Gradle wrapper to the latest stable release supported by the Kotlin and project plugins. Align the pinned Docker Gradle image, wrapper distribution, build scripts, and CI on the same release; remove the Java 25 workaround in `buildSrc` when the selected Gradle version supports that toolchain. Done when `./gradlew --version`, a clean local build, and the container build use the intended Gradle/JDK versions without compatibility warnings.
- Review and upgrade the complete version catalog with `./gradlew dependencyUpdates`, including Kotlin, Ktor, Apollo, jOOQ, Liquibase, PostgreSQL, HikariCP, Logback/SLF4J, Hoplite, Koin, Testcontainers, dotenv, Javassist, and all Gradle plugins. Apply breaking upgrades in isolated groups and document required source/config/migration changes. Done when no intentionally accepted update is undocumented and the full unit, route, migration, integration, and container verification suite passes from a clean checkout.
- Add an automated dependency-update workflow with grouped, scheduled proposals and CI verification. Keep Gradle wrapper updates separate from application-library updates and reject unstable versions unless explicitly approved. Done when a dry run produces reviewable groups without modifying generated jOOQ sources or publishing changes automatically.

## P3 - Maintainability and documentation

- Remove dead code such as the unused `filterTree` path.
- Update `README.md` and `db-migration/README.md` with the architecture, complete environment-variable list, migration workflow, production startup, API/error contract, health checks, and verification commands. Done when a new checkout can be launched and verified by following only the documentation.
