## Data Server module (Apache Geode)

This module is a **Spring Boot** application that provides an embedded Apache Geode Data Server with RESTful API.

### Features

- Embedded Geode Data Server that connects to a locator
- RESTful API for cluster management, region operations, and data operations
- Automatic lifecycle management with Spring Boot

### RESTful API Endpoints

#### Cluster Status
- `GET /api/cluster/status` - Get cluster status and member information

#### Region Management
- `GET /api/regions` - Get all region names
- `GET /api/regions/{regionName}` - Get region information
- `POST /api/regions/{regionName}?type=PARTITION` - Create a new region
- `DELETE /api/regions/{regionName}` - Delete a region

#### Data Operations
- `PUT /api/data/{regionName}/{key}` - Put data (Body: `{"value": "your value"}`)
- `GET /api/data/{regionName}/{key}` - Get data by key
- `DELETE /api/data/{regionName}/{key}` - Delete data by key
- `GET /api/data/{regionName}` - Get all keys in a region

### Configuration

The default configuration is in `src/main/resources/application.properties`.

Available properties:

- **`geode.dataserver.enabled`**: `true|false` (default: `true`)
- **`geode.dataserver.member-name`**: member name (default: `data-server`)
- **`geode.dataserver.working-dir`**: working directory (default: `target/data-server`)
- **`geode.dataserver.locator-host`**: locator host (default: `localhost`)
- **`geode.dataserver.locator-port`**: locator port (default: `10334`)
- **`server.port`**: web server port (default: `8080`)

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
   - The data server will connect to the locator and start the REST API on port 8080

### Run from Command Line

From repository root:

```bash
# Start locator first
./mvnw -pl locator spring-boot:run

# In another terminal, start data-server
./mvnw -pl data-server spring-boot:run
```

### Test the API

Once both services are running:

```bash
# Check cluster status
curl http://localhost:8080/api/cluster/status

# Create a region
curl -X POST "http://localhost:8080/api/regions/myRegion?type=PARTITION"

# Put data
curl -X PUT http://localhost:8080/api/data/myRegion/key1 \
  -H "Content-Type: application/json" \
  -d '{"value": "hello world"}'

# Get data
curl http://localhost:8080/api/data/myRegion/key1

# Get all keys
curl http://localhost:8080/api/data/myRegion
```

### Notes

- Make sure the locator is running before starting the data-server
- The JVM arguments for module access are configured in the run configurations
- If running from command line, the JVM arguments are configured in `pom.xml`

