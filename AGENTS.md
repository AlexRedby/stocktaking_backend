# Engineering guidelines

## Ownership and reuse

- Give every shared concern one clear owning module. Keep configuration classes,
  loaders, resources, and their tests together in the `configuration` module.
- Implement repeated behavior once and reuse it. For example, applications that
  load the repository `.env` file must use the shared `runWithDotEnv` mechanism.
- Add an abstraction, overload, dependency, or repository only when it serves a
  concrete requirement. Be prepared to explain the need, and prefer the simpler
  design when the extra mechanism is unnecessary.
- Do not strengthen validation or API contracts for hypothetical edge cases.
  Treat that as over-engineering unless a requirement or observed failure
  justifies it.

## Documentation

- Document stable contracts, behavior, and operating procedures. Do not preserve
  chat history, transient implementation rationale, or details relevant only to
  the current change.
- Do not maintain exhaustive inventories of files, classes, or tests in
  documentation; such inventories become stale as the project evolves.
- Keep detailed instructions next to the module that owns them. Higher-level
  README files should link to module documentation instead of duplicating it.
