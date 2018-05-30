# Hadoop on demand REST API

This project provides the REST API for the hadoop on demand project.

Currently the API allows to deploy the Hadoop clusters in OpenNebula.

It is based on Spring boot and for scaffolding we use [jhipster](http://jhipster.github.io)

To start the application use:
```
mvn spring-boot:run
```

# Usage
Get the code

    git clone ...

Add the org.opennebula.client.jar library

    cp -a org.opennebula.client.jar src/main/resources/lib

Add the org.opennebula.client.jar to the local maven repo so it can be packaged in the uberjar

    mvn install:install-file -Dfile=org.opennebula.client.jar -DgroupId=org.opennebula -DartifactId=client -Dversion=4.10.2 -Dpackaging=jar -DgeneratePom=true

Build skipping test: now they are failing

    mvn -Dmaven.test.skip=true package

    Building jar: hadoop-on-demand-rest-jhipster/target/hadooprest-0.1.0.jar

To run it using dev profile: src/main/resources/config/application-dev.yml

    java -jar hadooprest-0.1.0.jar

Or if you want the prod profile: src/main/resources/config/application-prod.yml

    java -jar hadooprest-0.1.0.jar --spring.profiles.active=prod

## MySQL/MariaDB

    docker run --name mariadb-hadooprest -e MYSQL_ROOT_PASSWORD=hadoop -e MYSQL_DATABASE=hadooprest -e MYSQL_USER=hadoop -e MYSQL_PASSWORD=hadoop -d mariadb:5.5
    docker run --name cloud --link mariadb-hadooprest:mysql -d cloud:0.1.0 

## cloud hadoop rest API

    docker run -p 8080:8080 -ti -d --link mariadb-hadooprest:mysql --name cloud1 docker-registry.cesga.es:5000/cloud:0.1.0

## Managing MariaDB container

    docker run -it --link some-mariadb:mysql --rm mariadb sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"$MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD"'
    docker exec -it some-mariadb bash
    docker logs some-mariadb


# OTHER USEFUL COMMANDS

## Standard way: Includes runing unit tests using maven-surefire

    mvn package

## For development instead of mvn package you can use

    mvn spring-boot:run

## Testing the API

    export TOKEN="x-auth-token: `curl -s -X POST http://localhost:8080/api/authenticate --data 'username=<USER>&password=<PASSWD>' | awk -F'"' '{print $4}'`"
    curl -H "$TOKEN" http://localhost:8080/api/users

    curl -H "$TOKEN" http://localhost:8080/api/ips
    curl -H "$TOKEN" http://localhost:8080/api/sshKeys

## OpenNebula

    scp -r /etc/one <container>:/etc/one
    # Use cloud.cesga.es priv address: 10.112.1.1
    #export ONE_XMLRPC=http://cloud.cesga.es:2633/RPC2
    export ONE_XMLRPC=http://10.112.1.1:2633/RPC2
    ONE_AUTH=./one_auth onevm list
