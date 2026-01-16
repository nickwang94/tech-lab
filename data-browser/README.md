# Data Browser Module

Spring Boot Web application for browsing and visualizing Apache Geode cluster data.

## Features

- **Cluster Status**: View cluster connection status, members, and information
- **Region Browser**: Browse all regions in the cluster with their metadata
- **Data Viewer**: View and explore data stored in regions
- **Modern UI**: Clean, responsive web interface similar to GemFire's native GUI

## Technology Stack

- **Spring Boot 3.4.1**: Web framework
- **Jetty**: Embedded web server (instead of Tomcat)
- **Apache Geode 1.15.2**: Client cache for connecting to GemFire cluster
- **Thymeleaf**: Server-side templating (optional)
- **Vanilla JavaScript**: Frontend framework

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# GemFire connection settings
geode.browser.locator-host=localhost
geode.browser.locator-port=10334
geode.browser.member-name=data-browser

# Web server settings
server.port=8081
```

## Running

1. Make sure the GemFire locator and data server are running
2. Start the application:
   ```bash
   mvn spring-boot:run -pl data-browser
   ```
3. Open browser: http://localhost:8081

## API Endpoints

### Cluster
- `GET /api/cluster/status` - Get cluster status and member information

### Regions
- `GET /api/regions` - Get all region names
- `GET /api/regions/info` - Get all regions with metadata
- `GET /api/regions/{regionName}` - Get specific region information

### Data
- `GET /api/data/{regionName}` - Get all data in a region (with pagination: `?limit=100&offset=0`)
- `GET /api/data/{regionName}/keys` - Get all keys in a region
- `GET /api/data/{regionName}/{key}` - Get specific key-value pair
- `POST /api/data/{regionName}/{key}` - Put data into region
- `DELETE /api/data/{regionName}/{key}` - Delete data from region

## Architecture

The application uses a **ClientCache** to connect to an existing GemFire cluster via locator. When accessing regions that are not yet proxied, the application automatically creates PROXY regions to access server-side data.

### Key Components

- **GemFireClientConfiguration**: Configures ClientCache connection to locator
- **ClusterService**: Provides cluster status and member information
- **RegionService**: Manages region metadata and information
- **DataService**: Handles data operations (get, put, delete, query)
- **Controllers**: REST API endpoints and view controllers
- **Frontend**: Single-page application with tabbed interface

## Notes

- The application connects as a **client** to the GemFire cluster, not as a peer or server
- Regions are automatically proxied when accessed if they don't exist locally
- Member information may be limited for client cache connections
- The UI auto-refreshes cluster status every 5 seconds
