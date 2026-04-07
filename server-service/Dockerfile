FROM amazoncorretto:23-alpine-jdk

WORKDIR /server-service
#ARG VERSION
ADD build/libs/server-service-0.0.1-SNAPSHOT.jar server-service.jar
#${VERSION}.jar server-service.jar

CMD ["java", "-jar", "server-service.jar"]
