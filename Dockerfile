FROM openjdk
ADD /build/libs/spring-webapp-0.0.1-SNAPSHOT.jar spring-webapp-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-webapp-0.0.1-SNAPSHOT.jar"]