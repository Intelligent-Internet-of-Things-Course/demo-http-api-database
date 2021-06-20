#!/bin/bash

docker run --name=api-mysql-database \
    -p 3306:3306 \
    -v $(pwd)/data:/var/lib/mysql \
    -e MYSQL_ROOT_PASSWORD=r00tT3st \
    -e MYSQL_DATABASE=api-manager \
    -e MYSQL_USER=api-manager \
    -e MYSQL_PASSWORD=apiT3st \
    -d mysql:8.0.25