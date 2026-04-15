FROM amazoncorretto:23-alpine-jdk

WORKDIR /voice-service
#ARG VERSION
ADD build/libs/voice-service-0.0.1-SNAPSHOT.jar voice-service.jar
#${VERSION}.jar voice-service.jar

CMD ["java", "-jar", "voice-service.jar"]
