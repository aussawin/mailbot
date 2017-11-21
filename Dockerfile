FROM openjdk:8u111-jdk-alpine
ADD demo-api.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]