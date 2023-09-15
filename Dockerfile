FROM openjdk:17
EXPOSE 1530
ADD target/DockerjenkinsCICD.jar DockerjenkinsCICD.jar
ENTRYPOINT [ "java","-jar","/DockerjenkinsCICD.jar" ]