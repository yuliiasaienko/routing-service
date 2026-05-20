# Land Routing Service

Spring Boot service that calculates a possible land route between two countries using `cca3` country codes.

## Requirements

- Java 17+
- Maven 3.9+

## Data Source

The project uses the provided country dataset and ships it as:

- `src/main/resources/countries.json`

Original source:

- https://raw.githubusercontent.com/mledoze/countries/master/countries.json

## Build

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run
```

Service starts on `http://localhost:8080`.

## Endpoint

```http
GET /routing/{origin}/{destination}
```

Example:

```bash
curl http://localhost:8080/routing/CZE/ITA
```

Response format:

```json
{
  "route": ["CZE", "AUT", "ITA"]
}
```

Notes:

- Returns one valid shortest land route (BFS).
- Returns HTTP `400` if no land route exists or an invalid `cca3` code is used.
