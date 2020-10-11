@echo off
start "V" java -Dserver.port=8080 -jar target/smpc-master-thesis-1.0-SNAPSHOT.jar
start "p1" java -Dserver.port=8081 -jar target/smpc-master-thesis-1.0-SNAPSHOT.jar
start "p2" java -Dserver.port=8082 -jar target/smpc-master-thesis-1.0-SNAPSHOT.jar
start "p3" java -Dserver.port=8083 -jar target/smpc-master-thesis-1.0-SNAPSHOT.jar
pause