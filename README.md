# Roach Shell

![Java CI with Maven](https://github.com/timveil-cockroach/roach-shell/workflows/Java%20CI%20with%20Maven/badge.svg)

Command line utility for CockroachDB written using [Spring Shell](https://projects.spring.io/spring-shell/).  Commands include:
* `connect` - connects to a CockroachDB cluster
* `disconnect` - disconnects from a CockroachDB cluster
* `hotspots` - detects range hot spots in a CockroachDB cluster often caused by poor primary key selection as described [here](https://www.cockroachlabs.com/docs/v19.2/performance-best-practices-overview.html#unique-id-best-practices)
* `clients` - list of clients currently connected to a CockroachDB cluster
* `statements` - filtered list of recent Statements run against a CockroachDB cluster

## Prerequisites
* Java 11 - `brew cask install adoptopenjdk11`.  You may need to run this first `brew tap AdoptOpenJDK/openjdk`.
* CockroachDB with the following:
    * a database `user` with the `admin` role assigned
    * `--listen-addr` and `--http-addr` accessible from machine running `roach-shell`

## To Build
```shell script
./mvnw clean package
```
This command will create an executable `jar` named `roach-shell-20.0.3-SNAPSHOT.jar` in the projects `target` directory.

## To Run
With Maven
```shell script
./mvnw spring-boot:run
```

With Java using the executable `jar` built above.
```shell script
java -jar roach-shell-20.0.3-SNAPSHOT.jar
````

To establish a connection to a CockroachDB cluster first run the `connect` command.  For example...

```shell script
roach-shell:>connect -h localhost

---------------------------------------------
The following configuration parameters will be used:
	host: localhost
	port: 26257
	database: system
	username: root
	password: (password is null or blank)
	ssl-enabled: false
	ssl-mode: disable
	ssl-root-crt-path:
	ssl-client-crt-path:
	ssl-client-key-path:
	http-scheme: http
	http-host: localhost
	http-port: 8080
	http-username:
	http-password: (password is null or blank)
---------------------------------------------

Connection to CockroachDB successful.  URL is jdbc:postgresql://localhost:26257/system?ApplicationName=HotSpotDetector&reWriteBatchedInserts=true&ssl=false&sslmode=disable
````

Once you are connected you can run other commands like `hotspots`.

## Command Help

### connect
To see the following content run `help connnect`.

```text
NAME
	connect - Connect to a CockroachDB cluster.

SYNOPSYS
	connect [--host] string  [[--port] int]  [[--database] string]  [[--username] string]  [[--password] string]  [[--ssl-mode] string]  [--ssl-enabled]  [[--ssl-root-crt-path] string]  [[--ssl-client-crt-path] string]  [[--ssl-key-path] string]  [[--http-username] string]  [[--http-password] string]  [[--http-host] string]  [[--http-port] int]  

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
		[Optional, default = <none>]

	--ssl-mode  string
		SSL mode for database connection.  disable, allow, prefer, require, verify-ca or verify-full.
		[Optional, default = disable]

	--ssl-enabled	is SSL enabled? true or false.
		[Optional, default = false]

	--ssl-root-crt-path  string
		path to Root Cert file when SSL is enabled
		[Optional, default = <none>]

	--ssl-client-crt-path  string
		path to SSL Client Cert file when SSL is enabled
		[Optional, default = <none>]

	--ssl-key-path  string
		path to SSL Client Key file when SSL is enabled
		[Optional, default = <none>]

	--http-username  string
		username used for Admin UI REST calls.  will use database username if no value provided.
		[Optional, default = <none>]

	--http-password  string
		password used for Admin UI REST calls.  will use database password if no value provided.
		[Optional, default = <none>]

	--http-host  string
		host used for Admin UI REST calls
		[Optional, default = <none>]

	--http-port  int
		port used for Admin UI REST calls
		[Optional, default = 8080]
```
#### Secure Connection Example
To connect to a secure cluster there are a number of security related parameters you can pass including `ssl-enabled`, `ssl-mode` and `ssl-root-crt-path`.  Note that most of the `http-*` parameters will default to their database equivalent.  In other words, if no `http-username` parameter is provided, the `connect` method will use the value set for `username`.

```shell script
connect -u [SOME USERNAME] --password [SOME PASSWORD] -h localhost --ssl-enabled --ssl-mode verify-full --ssl-root-crt-path /[SOME ABSOLUTE PATH]/ca.crt
```


### hotspots
To see the following content run `help hotspots`.

```text
NAME
	hotspots - Find range hot spots in a CockroachDB (CRDB) cluster.

SYNOPSYS
	hotspots [[--max-ranges] int]  [--verbose]

OPTIONS
	--max-ranges or -m  int
		max number of hot ranges returned
		[Optional, default = 10]

	--verbose or -v
		include verbose output.  true or false.
		[Optional, default = false]
```

### clients
To see the following content run `help clients`.

```text
NAME
	clients - List active client connections to the CockroachDB cluster.

SYNOPSYS
	clients [--verbose]  

OPTIONS
	--verbose or -v
		include verbose output.  true or false.
		[Optional, default = false]
```

### statements
To see the following content run `help statements`.

```text
NAME
	statements - List recent statements against the CockroachDB cluster.

SYNOPSYS
	statements [--dist-only]  [--exclude-ddl]  [--exclude-internal]  [--has-span-all]  [--verbose]  [[--app] string]  

OPTIONS
	--dist-only	include only dist sql statements.  true or false.
		[Optional, default = <none>]

	--exclude-ddl or -xd
		exclude DDL statements.  true or false.
		[Optional, default = <none>]

	--exclude-internal or -xi
		exclude statements from CockroachDB internals.  true or false.
		[Optional, default = <none>]

	--has-span-all	include statements with "span = ALL".  true or false.
		[Optional, default = <none>]

	--verbose or -v
		include verbose output.  true or false.
		[Optional, default = false]

	--app or -a  string
		only include statements from this application
		[Optional, default = <none>]

```

## Example Commands and Output

### hotspots
```text
roach-shell:>hotspots

---------------------------------------------
The following configuration parameters will be used:
	max-ranges: 10
	verbose: false
---------------------------------------------

Found Node with id [1], address [crdb-0:26257] and build [v20.1.6].
Found Node with id [2], address [crdb-2:26257] and build [v20.1.6].
Found Node with id [3], address [crdb-1:26257] and build [v20.1.6].
┏━━━━┳━━━━━━━━━━┳━━━━━━━━━━━━━━━┳━━━━━━━━┳━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃Rank┃QPS       ┃Node ID/Address┃Store ID┃Database┃Table                         ┃Index┃Range                                         ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃1   ┃7.491192  ┃1/crdb-0:26257 ┃1       ┃        ┃                              ┃     ┃/Table/SystemConfigSpan/Start - /Table/11     ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃2   ┃2.634822  ┃1/crdb-0:26257 ┃1       ┃        ┃                              ┃     ┃/Min - /System/NodeLiveness                   ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃3   ┃2.4772887 ┃3/crdb-1:26257 ┃3       ┃system  ┃rangelog                      ┃     ┃/Table/13 - /Table/14                         ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃4   ┃0.8675523 ┃1/crdb-0:26257 ┃1       ┃        ┃                              ┃     ┃/System/NodeLivenessMax - /System/tsd         ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃5   ┃0.6676972 ┃3/crdb-1:26257 ┃3       ┃        ┃                              ┃     ┃/System/NodeLiveness - /System/NodeLivenessMax┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃6   ┃0.6311181 ┃2/crdb-2:26257 ┃2       ┃        ┃                              ┃     ┃/System/tsd - /System/"tse"                   ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃7   ┃0.42551747┃1/crdb-0:26257 ┃1       ┃system  ┃eventlog                      ┃     ┃/Table/12 - /Table/13                         ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃8   ┃0.3371068 ┃1/crdb-0:26257 ┃1       ┃system  ┃reports_meta                  ┃     ┃/Table/28 - /Table/29                         ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃9   ┃0.2983412 ┃2/crdb-2:26257 ┃2       ┃system  ┃statement_diagnostics_requests┃     ┃/Table/35 - /Table/36                         ┃
┣━━━━╋━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃10  ┃0.2906859 ┃1/crdb-0:26257 ┃1       ┃system  ┃replication_stats             ┃     ┃/Table/27 - /Table/28                         ┃
┗━━━━┻━━━━━━━━━━┻━━━━━━━━━━━━━━━┻━━━━━━━━┻━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

### clients
```text
roach-shell:>clients

---------------------------------------------
The following configuration parameters will be used:
	verbose: false
---------------------------------------------

┏━━━━━━━━┳━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃Username┃Client Address  ┃Application Name┃Session Start             ┃Oldest Query Start        ┃Last Active Query                       ┃
┣━━━━━━━━╋━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃root    ┃172.18.0.5:58250┃HotSpotDetector ┃2020-10-07 19:04:11.951151┃2020-10-07 19:04:11.959122┃SET application_name = 'HotSpotDetector'┃
┗━━━━━━━━┻━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

### statements
```text
roach-shell:>statements

---------------------------------------------
The following configuration parameters will be used:
	application-name: null
	exclude-ddl: true
	exclude-internal: true
	dist-only: null
	has-span-all: null
	verbose: false
---------------------------------------------

Returned 71 total statements, 40 unique.  Showing 1 after applying filters.
┏━━━━┳━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃Node┃Application Name┃Execution Count┃Mean Latency (ms)┃Mean Row Count┃Last Plan Timestamp           ┃Statement                                                       ┃
┣━━━━╋━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃3   ┃HotSpotDetector ┃3              ┃5.55             ┃1             ┃2020-10-07T18:58:55.291562100Z┃SELECT * FROM crdb_internal.ranges_no_leases WHERE range_id = $1┃
┗━━━━┻━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

## To Release
```
./mvnw -B clean package release:clean release:prepare release:perform
```