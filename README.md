# Roach Shell
Command line utility for CockroachDB written using [Spring Shell](https://projects.spring.io/spring-shell/).  Commands include:
* `hotspots` - detects range hot spots in a CockroachDB cluster often caused by poor primary key selection as described [here](https://www.cockroachlabs.com/docs/v19.2/performance-best-practices-overview.html#unique-id-best-practices)

## Prerequisites
* Java 11 - `brew cask install adoptopenjdk11`.  You may need to run this first `brew tap AdoptOpenJDK/openjdk`.
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
To see the following content run `help hotspots`.

```text
NAME
	hotspots - Find range hot spots in a CockroachDB (CRDB) cluster.

SYNOPSYS
	hotspots [--host] string  [[--port] int]  [[--database] string]  [[--username] string]  [[--password] string]  [[--ssl-mode] string]  [--ssl-enabled]  [[--ssl-crt-path] string]  [[--ssl-key-path] string]  [[--http-scheme] string]  [[--http-username] string]  [[--http-password] string]  [[--http-host] string]  [[--http-port] int]  [[--max-ranges] int]  [--verbose]  

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
		SSL mode for database connection.  disable, allow, prefer, require, verify-ca or verify-full.
		[Optional, default = disable]

	--ssl-enabled	is SSL enabled? true or false.
		[Optional, default = false]

	--ssl-crt-path  string
		path to SSL Cert file when SSL is enabled
		[Optional, default = ]

	--ssl-key-path  string
		path to SSL Key file when SSL is enabled
		[Optional, default = ]

	--http-scheme  string
		HTTP scheme for Admin UI REST calls.  http or https.
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

	--max-ranges or -m  int
		max number of hot ranges returned
		[Optional, default = 10]

	--verbose	include verbose output.  true or false.
		[Optional, default = false]
```

## Example Command and Output
```text
roach-shell:>hotspots -h localhost -p 5432
---------------------------------------------
The following configuration parameters will be used:
	host: localhost
	port: 5432
	database: system
	username: root
	password: (password is null or blank)
	ssl-enabled: false
	ssl-mode: disable
	ssl-crt-path: null
	ssl-key-path: null
	http-scheme: http
	http-host: localhost
	http-port: 8080
	http-username: null
	http-password: (password is null or blank)
	verbose: false
---------------------------------------------
Found Node with id [1], address [crdb-0:26257] and build [v19.2.2].
Found Node with id [2], address [crdb-2:26257] and build [v19.2.2].
Found Node with id [3], address [crdb-1:26257] and build [v19.2.2].
┏━━━━┳━━━━━━━━━━━┳━━━━━━━━━━━━━━━┳━━━━━━━━┳━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃Rank┃QPS        ┃Node ID/Address┃Store ID┃Database┃Table                       ┃Index┃Range                                         ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃1   ┃0.9796118  ┃1/crdb-0:26257 ┃1       ┃        ┃                            ┃     ┃/Table/SystemConfigSpan/Start - /Table/11     ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃2   ┃0.6825487  ┃1/crdb-0:26257 ┃1       ┃        ┃                            ┃     ┃/System/NodeLiveness - /System/NodeLivenessMax┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃3   ┃0.644147   ┃2/crdb-2:26257 ┃2       ┃        ┃                            ┃     ┃/System/tsd - /System/"tse"                   ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃4   ┃0.40683445 ┃1/crdb-0:26257 ┃1       ┃        ┃                            ┃     ┃/System/NodeLivenessMax - /System/tsd         ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃5   ┃0.25467408 ┃2/crdb-2:26257 ┃2       ┃system  ┃reports_meta                ┃     ┃/Table/28 - /Max                              ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃6   ┃0.10287662 ┃1/crdb-0:26257 ┃1       ┃system  ┃jobs                        ┃     ┃/Table/15 - /Table/16                         ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃7   ┃0.034401648┃1/crdb-0:26257 ┃1       ┃system  ┃lease                       ┃     ┃/Table/11 - /Table/12                         ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃8   ┃0.020126306┃2/crdb-2:26257 ┃2       ┃        ┃                            ┃     ┃/Min - /System/NodeLiveness                   ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃9   ┃0.016861927┃3/crdb-1:26257 ┃3       ┃system  ┃replication_stats           ┃     ┃/Table/27 - /Table/28                         ┃
┣━━━━╋━━━━━━━━━━━╋━━━━━━━━━━━━━━━╋━━━━━━━━╋━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━╋━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫
┃10  ┃0.016764492┃2/crdb-2:26257 ┃2       ┃system  ┃replication_constraint_stats┃     ┃/Table/25 - /Table/26                         ┃
┗━━━━┻━━━━━━━━━━━┻━━━━━━━━━━━━━━━┻━━━━━━━━┻━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```