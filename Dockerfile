FROM openjdk:11
EXPOSE 8080
ADD target/tweet-app-sk.jar tweet-app-sk.jar
ENTRYPOINT ["java","-jar","/tweet-app-sk.jar"]