# Roach Shell
Command line utility for CockroachDB written using [Spring Shell](https://projects.spring.io/spring-shell/).  Commands include:
* `hotspots` - detects range hot spots in a CockroachDB cluster often caused by poor primary key selection as described [here](https://www.cockroachlabs.com/docs/v19.2/performance-best-practices-overview.html#unique-id-best-practices)

## Prerequisites
* Java 11 - `brew cask install adoptopenjdk11`
* CockroachDB with the following:
    * a database `user` with the `admin` role assigned
    * `--listen-addr` and `--http-addr` accessible from machine running `roach-shell`

## To Build
```shell script
./mvnw clean package
```
This command will create an executable `jar` named `roach-shell-0.0.1-SNAPSHOT.jar` in the projects `target` directory.

## To Run
With Maven
```shell script
./mvnw spring-boot:run
```

With Java using the executable `jar` built above.
```shell script
java -jar roach-shell-0.0.1-SNAPSHOT.jar
````

## Command Help
```text
NAME
	hotspots - Find range hot spots in a CockroachDB (CRDB) cluster.

SYNOPSYS
	hotspots [--host] string  [[--port] int]  [[--database] string]  [[--username] string]  [[--password] string]  [[--ssl-mode] string]  [--ssl-enabled]  [[--ssl-crt-path] string]  [[--ssl-key-path] string]  [[--http-scheme] string]  [[--http-username] string]  [[--http-password] string]  [[--http-host] string]  [[--http-port] int]  [[--maxRanges] int]  

OPTIONS
	--host or -h  string
		hostname of a CRDB node
		[Mandatory]

	--port or -p  int
		port of a CRDB node
		[Optional, default = 26257]

	--database or -d  string
		CRDB database name
		[Optional, default = system]

	--username or -u  string
		username used to connect to database
		[Optional, default = root]

	--password  string
		password used to connect to database
		[Optional, default = ]

	--ssl-mode  string
		SSL mode for database connection
		[Optional, default = disable]

	--ssl-enabled	is SSL enabled?
		[Optional, default = false]

	--ssl-crt-path  string
		path to SSL Cert file when SSL is enabled
		[Optional, default = ]

	--ssl-key-path  string
		path to SSL Key file when SSL is enabled
		[Optional, default = ]

	--http-scheme  string
		HTTP scheme for Admin UI REST calls
		[Optional, default = http]

	--http-username  string
		username used for Admin UI REST calls
		[Optional, default = ]

	--http-password  string
		password used for Admin UI REST calls
		[Optional, default = ]

	--http-host  string
		host used for Admin UI REST calls
		[Optional, default = ]

	--http-port  int
		port used for Admin UI REST calls
		[Optional, default = 8080]

	--maxRanges or -m  int
		max number of hot ranges returned
		[Optional, default = 10]
```

## Example Commands
```
hotspots --host localhost --port 5432
```
```
hotspots -h localhost -p 5432
```