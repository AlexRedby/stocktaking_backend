# TODO

Priorities: P1 blocks safe or correct use; P2 is required for reliability; P3 is cleanup or optimization.

## P2 - Tests and contract gates

- Add unit tests for phrase search, graph traversal with cycles/diamonds, loop removal, duplicate recipes, recipe quantities, station handles, and craftable-item filtering.
- Add a cross-repository smoke test for autocomplete and crafting-tree rendering through the supported production proxy/origin topology. Done when the backend test/check task and the shared smoke test run in CI from a clean checkout.

## P3 - Maintainability and documentation

- Update `README.md` and `db-migration/README.md` with the architecture, complete environment-variable list, migration workflow, production startup, API/error contract, health checks, and verification commands. Done when a new checkout can be launched and verified by following only the documentation.
