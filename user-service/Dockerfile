FROM amazoncorretto:23-alpine-jdk

WORKDIR /user-service
#ARG VERSION
ADD build/libs/user-service-0.0.1-SNAPSHOT.jar user-service.jar
#${VERSION}.jar user-service.jar

CMD ["java", "-jar", "user-service.jar"]
