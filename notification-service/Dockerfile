FROM amazoncorretto:23-alpine-jdk

WORKDIR /notification-service
#ARG VERSION
ADD build/libs/notification-service-0.0.1-SNAPSHOT.jar notification-service.jar
#${VERSION}.jar notification-service.jar

CMD ["java", "-jar", "notification-service.jar"]
