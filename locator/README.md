## Locator module (Apache Geode)

This module is a **Spring Boot** application that is intended for learning and experimenting with an **Apache Geode Locator**.

### What is a Locator?

A Geode Locator is a lightweight process that helps members discover each other and join a distributed system. Common responsibilities include:

- **Discovery**: provides connection information (host/port) for members joining the cluster.
- **Membership coordination**: helps form the distributed system.
- **Client discovery**: clients can use locator endpoints to find servers.

### How this module is implemented

This module starts an **embedded Locator** using Geode's `LocatorLauncher` and ties it to the Spring application lifecycle:

- `LocatorApplication`: Spring Boot entry point.
- `LocatorConfiguration`: wires the embedded locator lifecycle bean.
- `EmbeddedLocatorLifecycle`: starts/stops `LocatorLauncher` as the Spring context starts/stops.
- `LocatorProperties`: externalized configuration under the prefix `geode.locator.*`.

### Configuration

The default configuration is in `src/main/resources/application.properties`.

Available properties:

- **`geode.locator.enabled`**: `true|false` (default: `true`)
- **`geode.locator.port`**: locator port (default: `10334`)
- **`geode.locator.member-name`**: member name (default: `locator`)
- **`geode.locator.working-dir`**: working directory (default: `target/locator`)

### Run

From repository root:

```bash
./mvnw -pl locator spring-boot:run
```

Run without starting a real locator (useful for quickly verifying the app wiring):

```bash
./mvnw -pl locator spring-boot:run -Dspring-boot.run.arguments="--geode.locator.enabled=false"
```

### Test

```bash
./mvnw test
```

### Notes and next steps

- This module focuses on **local development** and **learning**.
- For more advanced experiments, consider adding:
  - **GFSH scripts** for starting a locator/server externally
  - **Health endpoints** that report locator status
  - **Multiple-locator** setups (ports, working dirs, and discovery)
  - **Security** (SSL, authentication) and **WAN** scenarios
