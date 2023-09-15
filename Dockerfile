FROM openjdk:17
EXPOSE 1530
ADD target/DockerjenkinsCICD.jar DockerjenkinsCICD.jar
ENTRYPOINT [ "java","-jar","/SpringOAuth-0.0.1-SNAPSHOT.jar" ]