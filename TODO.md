# TODO

Priorities: P1 blocks safe or correct use; P2 is required for reliability; P3 is cleanup or optimization.

## P2 - Runtime reliability

- Make the container/build inputs reproducible: pin the Gradle builder image to an exact supported version and run tests in the image/CI instead of using `-x test`. Done when a clean container build uses the wrapper-compatible version and executes the complete verification suite.

## P2 - Tests and contract gates

- Add unit tests for phrase search, graph traversal with cycles/diamonds, loop removal, duplicate recipes, recipe quantities, station handles, and craftable-item filtering.
- Add Ktor route tests for all supported API success and error responses.
- Add a cross-repository smoke test for autocomplete and crafting-tree rendering through the supported production proxy/origin topology. Done when the backend test/check task and the shared smoke test run in CI from a clean checkout.

## P2 - Dependency and toolchain upgrades

- Upgrade the Gradle wrapper to the latest stable release supported by the Kotlin and project plugins. Align the pinned Docker Gradle image, wrapper distribution, build scripts, and CI on the same release; remove the Java 25 workaround in `buildSrc` when the selected Gradle version supports that toolchain. Done when `./gradlew --version`, a clean local build, and the container build use the intended Gradle/JDK versions without compatibility warnings.
- Review and upgrade the complete version catalog with `./gradlew dependencyUpdates`, including Kotlin, Ktor, Apollo, jOOQ, Liquibase, PostgreSQL, HikariCP, Logback/SLF4J, Hoplite, Koin, Testcontainers, dotenv, Javassist, and all Gradle plugins. Apply breaking upgrades in isolated groups and document required source/config/migration changes. Done when no intentionally accepted update is undocumented and the full unit, route, migration, integration, and container verification suite passes from a clean checkout.
- Add an automated dependency-update workflow with grouped, scheduled proposals and CI verification. Keep Gradle wrapper updates separate from application-library updates and reject unstable versions unless explicitly approved. Done when a dry run produces reviewable groups without modifying generated jOOQ sources or publishing changes automatically.

## P3 - Maintainability and documentation

- Remove dead code such as the unused `filterTree` path.
- Update `README.md` and `db-migration/README.md` with the architecture, complete environment-variable list, migration workflow, production startup, API/error contract, health checks, and verification commands. Done when a new checkout can be launched and verified by following only the documentation.
