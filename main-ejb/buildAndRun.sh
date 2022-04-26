#!/bin/sh
mvn clean package && docker build -t com.mycompany/concurrency2 .
docker rm -f concurrency2 || true && docker run -d -p 8080:8080 -p 4848:4848 --name concurrency2 com.mycompany/concurrency2 
