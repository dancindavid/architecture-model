# Build
mvn clean package && docker build -t com.mycompany/concurrency1 .

# RUN

docker rm -f concurrency1 || true && docker run -d -p 8080:8080 -p 4848:4848 --name concurrency1 com.mycompany/concurrency1 