# metrics-monitoring
### Overview 

There is a warehouse equipped with various types of sensors that monitor environmental
conditions. These sensors provide measurements such as current temperature and
humidity, which are transmitted via UDP. The warehouse service interacts with all these
sensors and automatically publishes the measurements to a central monitoring service. This
service oversees multiple warehouses and activates an alarm if temperature or humidity
readings exceed configured thresholds.

## High level architecture
```mermaid
graph TD;
    Kafka-->central-service;
   
    warehouse-service-1-->Kafka;
    warehouse-service-2-->Kafka;
   
    sensor-t1-->warehouse-service-1;
    sensor-h1-->warehouse-service-1;
    sensor-t2-->warehouse-service-2;
    sensor-h2-->warehouse-service-2;
```

## Steps to run:
+ Build
```shell 
mvn clean package
```
+ Build warehouse-service docker images 
```shell 
cd warehouse-service 
docker build -t 'warehouse-service' . 
```
+ Build central-service docker images
```shell 
cd ..
cd central-service 
docker build -t 'central-service' .
```
+ Run docker compose
```shell
cd ..
docker compose run
```
+ Run shell to send udp datagram:
```shell
seq 100 | xargs -I{} echo "sensor_id=t{}; value={}" | nc -u -w1 localhost 3344
seq 100 | xargs -I{} echo "sensor_id=t{}; value={}" | nc -u -w1 localhost 3355
```