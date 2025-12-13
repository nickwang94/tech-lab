## Geode Lab

This repository is a **Spring Boot multi-module** lab for learning Apache Geode.

### Modules

- **`locator`**: A Spring Boot application used to experiment with running an embedded Apache Geode Locator.

### Repository conventions

- **All Markdown files** must be written in English.
- **All code comments** must be written in English.

### Build and test

```bash
./mvnw test
```

### Run the locator module

```bash
./mvnw -pl locator spring-boot:run
```

To start the app without actually starting a Geode Locator:

```bash
./mvnw -pl locator spring-boot:run -Dspring-boot.run.arguments="--geode.locator.enabled=false"
```
