# Build
mvn clean package && docker build -t com.mycompany/Weatherman .

# RUN

docker rm -f Weatherman || true && docker run -d -p 8080:8080 -p 4848:4848 --name Weatherman com.mycompany/Weatherman 