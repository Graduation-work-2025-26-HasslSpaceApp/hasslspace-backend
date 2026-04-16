FROM amazoncorretto:23-alpine-jdk

WORKDIR /chat-service
#ARG VERSION
ADD build/libs/chat-service-0.0.1-SNAPSHOT.jar chat-service.jar
#${VERSION}.jar chat-service.jar

CMD ["java", "-jar", "chat-service.jar"]
