FROM openjdk:17
EXPOSE 1119
ADD target/DockerjenkinsCICD.jar DockerjenkinsCICD.jar
ENTRYPOINT [ "java","-jar","/DockerjenkinsCICD.jar" ]