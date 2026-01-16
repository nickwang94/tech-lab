## Data Server module (Apache Geode)

This module is a **Spring Boot** application that provides an embedded Apache Geode Data Server.

### Features

- Embedded Geode Data Server that connects to a locator
- Automatic lifecycle management with Spring Boot
- Pure data server without web interface (use `data-browser` module for UI and API access)

### Configuration

The default configuration is in `src/main/resources/application.properties`.

Available properties:

- **`geode.dataserver.enabled`**: `true|false` (default: `true`)
- **`geode.dataserver.member-name`**: member name (default: `data-server`)
- **`geode.dataserver.working-dir`**: working directory (default: `target/data-server`)
- **`geode.dataserver.locator-host`**: locator host (default: `localhost`)
- **`geode.dataserver.locator-port`**: locator port (default: `10334`)

### Run in IntelliJ IDEA

1. **Import the run configurations** (if not already imported):
   - The run configurations are in `.idea/runConfigurations/`
   - IDEA should automatically detect them
   - If not, go to Run → Edit Configurations → Import from `.idea/runConfigurations/`

2. **Start Locator first**:
   - Select "LocatorApplication" from the run configuration dropdown
   - Click Run (or press Shift+F10)
   - Wait for locator to start (usually takes a few seconds)

3. **Start Data Server**:
   - Select "DataServerApplication" from the run configuration dropdown
   - Click Run (or press Shift+F10)
   - The data server will connect to the locator and join the cluster

### Run from Command Line

From repository root:

```bash
# Start locator first
./mvnw -pl locator spring-boot:run

# In another terminal, start data-server
./mvnw -pl data-server spring-boot:run
```

### Accessing the Data Server

To access the data server and browse data, use the **data-browser** module which provides:
- Web UI for browsing cluster, regions, and data
- RESTful API for all operations
- Runs on port 8081 by default

### Notes

- Make sure the locator is running before starting the data-server
- The JVM arguments for module access are configured in the run configurations
- If running from command line, the JVM arguments are configured in `pom.xml`

