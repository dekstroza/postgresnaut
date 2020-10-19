# Postgresnaut - Micronaut & GraalVM example
![Java CI with Maven](https://github.com/dekstroza/postgresnaut/workflows/Java%20CI%20with%20Maven/badge.svg)


## Description

Micronaut and Graal example service using PostgreSQL as the datastore compiled into statically or dynamically linked native image.
This is a slightly more comprehensive hello world type of a microservice built with micronaut, using postgresql as its data store. It can be compiled as tradidtional java application and packaged into docker container as runnable jar, or as graalvm compiled native binary (both statically and dynamically linked) and packaged into docker container.

## Features

Features demonstrated are: compilation to native image (with static linking) jpa/hibernate data repository, versioned api, jaeger based tracing, prometheus metric and open-api. Helm chart provided will deploy built docker image along with postgresql, prometheus and all-in-one jaeger demonstrating all of the bespoke features. There is also a demo grafana dashboard provided with the helm chart, which will be installed into grafana upon chart deployment.

## Implementations

Beside this implementation which is using hibernate and jpa data repository approach, there is another branch *micronaut-data-repository* with same code and capabilities, just realized with the micronaut's native jdbc data repository. Both branches use same helm charts and offer same functionailty, only difference is the implementation choice for db interaction.

## Requirements

1. Java with Graal, can be install with sdkman (https://sdkman.io)
2. Maven
3. Docker
4. Kubernetes
5. Helm

**Please be aware that native image compilation is done inside docker container, compilation takes a while and requires resources (CPU,MEM) so adjust your docker resource limits if running on Mac or Windows.**

Easiest way to run this demo is to install sdkman, and using it install java (graal, latest) and micronaut (latest). The instructions assume k8s on Docker for Mac/Windows (k8s installed with docker itself). This makes exposing services through LoadBalancer easy, as they are exposed on localhost.


## Building with maven

- Runnable jar build
  ```
  ## Skip all graalvm and docker stuff and just build runnable jar
  ./mvnw clean install -Ddockerfile.skip
  ```
- Build docker image with runnable jar
  ```
  ## Skip all graalvm and docker stuff and just build runnable jar
  ./mvnw clean install
  ```
- Build docker image with dynamically linked native image
  ```
  ## Build dynamically linked native image
  ./mvnw clean install -Ddynamic
  ```
- Build docker image with statically linked native image
  
  In order to build statically linked native images, we need extra step first: we need to add support for libmuslc to the oracle's builder image. 
  To do this run:
  ```
  cd builder-image
  ./build.sh
  ```
  This will build the helper image, used later when the project is compiled and packaged into its docker container. 
  Alternatively, the helper image will be pulled from my docker repository located here **dekstroza/graalvm-ce:20.2.0-r11-libmuslc**
  ```
  ## Now run to create statically linked native image and package it into docker container
  ./mvnw clean install -Dstatic
  ```

## Trying out the service

Service can be deployed using provided helm charts in k8s directory with
```
helm install --generate-name k8s/postgresnaut
```
This will deploy two instances of the service, postgresql, prometheus, grafana and jaeger-all-in-one (for demo purpose). It
will also configure grafana to use prometheus as datasource, and automatically
add application dashboard to the grafana.
Service is annotated for prometheus in helm charts, and will automatically be
scraped by prometheus.

After deploying helm chart, follow instructions printed by helm to obtain
grafana admin password, and access grafana with browser. Follow the instructions in the Notes printed after helm chart is deployed.

Alternatively after deploying service with helm command above, more instances
can be started with "normal" jvm for comparision examples, using the following commands:
```
# Expose postgresql on loadbalancer
SERVICE_NAME=$(kubectl get svc | grep 'postgresnaut\-[0-9]*\-postgresql ' | awk '{print $1}'); kubectl expose svc $SERVICE_NAME --name $SERVICE_NAME-balanced --type LoadBalancer --port 27017 --target-port 27017
java -jar target/postgresnaut-1.0.0-SNAPSHOT.jar
```
This can be usefull to compare startup times, and pre and post jvm warmup performance differences.

### Some common commands:

Testing the service from command line using curl:

```bash
# Save alarm to the database
curl -X POST localhost:7777/postgresnaut/alarms -d '{"name": "Second Alarm", "severity": "MEDIUM"}' -H 'Content-Type:application/json'
# Get all alarms
curl -v localhost:7777/postgresnaut/alarms
# Health endpoint
curl -v localhost:7777/health
# Prometheus metric endpoint
curl -v localhost:7777/prometheus
```
Timing the service responses using curl and command line:

1. Create file curl-format.txt
2. Test the service with curl command

Content of the curl-format.txt:
```
      time_namelookup:  %{time_namelookup}\n
         time_connect:  %{time_connect}\n
      time_appconnect:  %{time_appconnect}\n
     time_pretransfer:  %{time_pretransfer}\n
        time_redirect:  %{time_redirect}\n
   time_starttransfer:  %{time_starttransfer}\n
                    ----------\n
            time_total:  %{time_total}\n
``` 
Command to test the latency (get all alarms, or similar for save alarm url):
```
# For get all alarms
curl -w "@curl-format.txt" -o /dev/null -s "http://localhost:7777/postgresnaut/alarms"
# For save alarm
curl -w "@curl-format.txt" -o /dev/null -s -X POST localhost:7777/postgresnaut/alarms -d '{"name": "Second Alarm", "severity": "MEDIUM"}' -H 'Content-Type:application/json'
# Save several alarms
for i in {10..20}; do curl -X POST localhost:7777/postgresnaut/alarms -d "{\"name\": \"Second Alarm\", \"severity\": \"MEDIUM\"}" -H 'Content-Type:application/json'; done
```

## Exposed endpoints
```
Grafana is available on http://localhost:8769/ username is admin and the password can be obtained following instructions printed after deploying helm chart.
```

```
Demo dashboard is provided with the Grafana, providing some basic telemetry on cpu, mem and api calls.
```

```
Jaeger UI is on: localhost:80 and shows some useless (in this example) spans, provided more as illustration how to do it.
```

```
Openapi definition can be accessed at: http://localhost:7777/swagger/micronaut-service-1.0.0.yml
```

```
Swagger ui is available at: http://localhost:7777/swagger-ui
```

```
Redoc is available at:http://localhost:7777/redoc
```

```
Rapidoc is available at: http://localhost:7777/rapidoc
```

In order to reduce memory consumption of graalvm native images, few netty related arguments are passed to the application binary on startup (see netty documentation for more info)


Happy hacking...

