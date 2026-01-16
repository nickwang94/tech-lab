# Apache Geode Lab

A **Spring Boot multi-module** project for learning and experimenting with Apache Geode distributed caching system.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Modules](#modules)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Development](#development)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Repository Conventions](#repository-conventions)

## 🎯 Overview

This lab provides a complete Apache Geode setup with:

- **Embedded Locator**: Service discovery and cluster coordination
- **Data Server**: Distributed cache server with management API
- **Data Browser**: Web-based GUI for browsing and managing Geode cluster data
- **Spring Boot Integration**: Automatic lifecycle management
- **RESTful APIs**: Easy-to-use HTTP endpoints for cluster and data management

## 🏗️ Architecture

```
┌─────────────────┐
│   Locator       │  ← Service discovery & cluster coordination
│   (Port 10334)  │
└────────┬────────┘
         │
         │ connects to
         │
┌────────▼────────┐      ┌──────────────────┐
│  Data Server    │◄─────┤  Data Browser    │
│  (Port 8080)    │      │  (Port 8081)     │
│                 │      │  - Web UI        │
│  - Management   │      │  - REST API      │
│    API          │      │  - Jetty Server  │
└─────────────────┘      └──────────────────┘
```

### Component Flow

1. **Locator** starts first and provides discovery service
2. **Data Server** connects to Locator and joins the cluster, provides management API
3. **Data Browser** connects to Locator as a client and provides Web UI + REST API
4. **Data Browser** calls Data Server's management API for region creation/deletion

## 📦 Modules

### `locator` - Geode Locator Module

A Spring Boot application that runs an embedded Apache Geode Locator.

**Key Features:**
- Service discovery for cluster members
- Membership coordination
- Client discovery endpoints

**See:** [locator/README.md](locator/README.md) for detailed documentation.

### `data-server` - Geode Data Server Module

A Spring Boot application that provides an embedded Geode Data Server with management API.

**Key Features:**
- Connects to Locator automatically
- Management API for region operations (create/delete)
- Pure data server (no web interface)

**See:** [data-server/README.md](data-server/README.md) for detailed documentation.

### `data-browser` - Geode Data Browser Module

A Spring Boot Web application (using Jetty) that provides a modern web interface for browsing and managing Geode cluster data.

**Key Features:**
- Web-based GUI similar to GemFire's native GUI
- Cluster status visualization
- Region browser and management
- Data viewer with pagination
- RESTful API for all operations
- Connects to cluster via Locator (ClientCache)
- Calls Data Server for region management operations

**See:** [data-browser/README.md](data-browser/README.md) for detailed documentation.

## 📋 Prerequisites

- **Java 21+** (tested with Java 21)
- **Maven 3.6+** (or use included `mvnw`)
- **IntelliJ IDEA** (optional, for IDE support)

## 🚀 Quick Start

### 1. Start the Locator

```bash
# From repository root
./mvnw -pl locator spring-boot:run
```

Wait for the locator to start (usually takes a few seconds). You should see:
```
Locator in /path/to/target/locator on localhost[10334] as locator is currently online.
```

### 2. Start the Data Server

In a **new terminal**:

```bash
./mvnw -pl data-server spring-boot:run
```

The data server will connect to the locator and start the management API on port 8080.

### 3. Start the Data Browser

In a **new terminal**:

```bash
./mvnw -pl data-browser spring-boot:run
```

The data browser will connect to the locator and start the web interface on port 8081.

### 4. Access the Web Interface

Open your browser and navigate to:
```
http://localhost:8081
```

You can now:
- View cluster status and members
- Browse regions
- Create new regions (with type descriptions)
- View and browse data in regions

### 5. Test the APIs

**Data Browser API (Port 8081):**
```bash
# Check cluster status
curl http://localhost:8081/api/cluster/status

# Get all regions
curl http://localhost:8081/api/regions

# Create a region
curl -X POST "http://localhost:8081/api/regions/myRegion?type=PARTITION"

# Get region data
curl http://localhost:8081/api/data/myRegion
```

**Data Server Management API (Port 8080):**
```bash
# Create a region (called by data-browser)
curl -X POST "http://localhost:8080/management/regions/myRegion?type=PARTITION"
```

## 📚 API Documentation

### Data Browser API (Port 8081)

#### Cluster Status

**`GET /api/cluster/status`**

Get cluster status and member information.

**Response:**
```json
{
  "status": "connected",
  "cacheName": "DEFAULT",
  "memberCount": 2,
  "members": [
    {
      "id": "...",
      "name": "locator",
      "host": "localhost",
      "groups": []
    },
    {
      "id": "...",
      "name": "data-server",
      "host": "localhost",
      "groups": []
    }
  ]
}
```

#### Region Management

**`GET /api/regions`**

Get all region names.

**`GET /api/regions/info`**

Get all regions with metadata.

**`GET /api/regions/{regionName}`**

Get detailed information about a specific region.

**`POST /api/regions/{regionName}?type=PARTITION`**

Create a new region. The request is forwarded to data-server.

**Query Parameters:**
- `type` (optional): Region type (default: `PARTITION`)
  - Valid values: `PARTITION`, `REPLICATE`, `PARTITION_PERSISTENT`, `REPLICATE_PERSISTENT`, `PARTITION_REDUNDANT`, `REPLICATE_HEAP_LRU`, `PARTITION_HEAP_LRU`

**`DELETE /api/regions/{regionName}`**

Delete a region.

#### Data Operations

**`GET /api/data/{regionName}`**

Get all data in a region (with pagination: `?limit=100&offset=0`).

**`GET /api/data/{regionName}/keys`**

Get all keys in a region.

**`GET /api/data/{regionName}/{key}`**

Get a specific key-value pair.

**`POST /api/data/{regionName}/{key}`**

Put data into a region.

**`DELETE /api/data/{regionName}/{key}`**

Delete data from a region.

### Data Server Management API (Port 8080)

**`POST /management/regions/{regionName}?type=PARTITION`**

Create a new region on the server.

**`DELETE /management/regions/{regionName}`**

Delete a region from the server.

## 💻 Development

### Using IntelliJ IDEA

1. **Import Run Configurations**
   - Run configurations are in `.idea/runConfigurations/`
   - IDEA should automatically detect them
   - If not: Run → Edit Configurations → Import from `.idea/runConfigurations/`

2. **Start Locator**
   - Select "LocatorApplication" from run configuration dropdown
   - Click Run (or press `Shift+F10`)
   - Wait for startup to complete

3. **Start Data Server**
   - Select "DataServerApplication" from run configuration dropdown
   - Click Run (or press `Shift+F10`)

4. **Start Data Browser**
   - Select "DataBrowserApplication" from run configuration dropdown
   - Click Run (or press `Shift+F10`)
   - Open browser to http://localhost:8081

**Note:** JVM arguments for Java module system are pre-configured in the run configurations.

### Build and Test

```bash
# Build all modules
./mvnw clean install

# Run tests
./mvnw test

# Build specific module
./mvnw -pl locator clean install
./mvnw -pl data-server clean install
./mvnw -pl data-browser clean install
```

### Running Without Geode

For quick testing without starting actual Geode components:

```bash
# Run locator without starting Geode Locator
./mvnw -pl locator spring-boot:run \
  -Dspring-boot.run.arguments="--geode.locator.enabled=false"

# Run data-server without connecting to Geode
./mvnw -pl data-server spring-boot:run \
  -Dspring-boot.run.arguments="--geode.dataserver.enabled=false"
```

## ⚙️ Configuration

### Locator Configuration

Edit `locator/src/main/resources/application.properties`:

```properties
geode.locator.enabled=true
geode.locator.port=10334
geode.locator.member-name=locator
geode.locator.working-dir=target/locator
```

### Data Server Configuration

Edit `data-server/src/main/resources/application.properties`:

```properties
geode.dataserver.enabled=true
geode.dataserver.member-name=data-server
geode.dataserver.working-dir=target/data-server
geode.dataserver.locator-host=localhost
geode.dataserver.locator-port=10334
server.port=8080
```

### Data Browser Configuration

Edit `data-browser/src/main/resources/application.properties`:

```properties
geode.browser.locator-host=localhost
geode.browser.locator-port=10334
geode.browser.member-name=data-browser
geode.browser.data-server-url=http://localhost:8080
server.port=8081
```

### JVM Arguments

For Java 21+, the following JVM arguments are required (already configured in `pom.xml` and IDEA run configurations):

```bash
--add-opens java.base/sun.nio.ch=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
--add-opens java.base/java.lang.invoke=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.util.concurrent=ALL-UNNAMED
--add-opens java.base/java.net=ALL-UNNAMED
--add-opens java.base/java.text=ALL-UNNAMED
--add-opens java.desktop/java.awt.font=ALL-UNNAMED
```

## 🔧 Troubleshooting

### Issue: `IllegalAccessError` when starting

**Solution:** Ensure JVM arguments are configured. They are automatically included when using Maven or IDEA run configurations.

### Issue: Data Server cannot connect to Locator

**Solutions:**
1. Ensure Locator is running first
2. Check `geode.dataserver.locator-host` and `geode.dataserver.locator-port` match Locator configuration
3. Verify Locator is listening on the correct port: `netstat -an | grep 10334`

### Issue: Data Browser cannot connect to Locator

**Solutions:**
1. Ensure Locator is running
2. Check `geode.browser.locator-host` and `geode.browser.locator-port` match Locator configuration
3. Verify Data Server is running (required for region management)

### Issue: `ClassCastException` with proxy classes

**Solution:** This has been fixed in the current implementation. Services use `CacheFactory.getAnyInstance()` directly instead of injected beans.

### Issue: Port already in use

**Solutions:**
1. Change port in `application.properties`
2. Kill the process using the port:
   ```bash
   # Find process
   lsof -i :10334  # for locator
   lsof -i :8080   # for data-server
   lsof -i :8081   # for data-browser
   
   # Kill process
   kill -9 <PID>
   ```

### Issue: Created region not showing in Data Browser

**Solution:** The Data Browser automatically creates a PROXY region after creation. If it doesn't appear, try refreshing the regions list or check the browser console for errors.

## 📝 Repository Conventions

- **All Markdown files** must be written in English
- **All code comments** must be written in English
- **Commit messages** follow conventional commits format

## 🗺️ Project Structure

```
tech-lab/
├── locator/              # Locator module
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── data-server/          # Data Server module
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── data-browser/         # Data Browser module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   ├── resources/
│   │   │   │   ├── static/    # CSS, JS
│   │   │   │   └── templates/ # HTML templates
│   │   │   └── ...
│   ├── pom.xml
│   └── README.md
├── .idea/
│   └── runConfigurations/  # IDEA run configurations
├── pom.xml               # Parent POM
└── README.md             # This file
```

## 📖 Additional Resources

- [Apache Geode Documentation](https://geode.apache.org/docs/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Module-specific READMEs](locator/README.md) | [data-server/README.md](data-server/README.md) | [data-browser/README.md](data-browser/README.md)

## 📄 License

See [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Write tests
4. Submit a pull request

---

**Happy Learning! 🚀**
