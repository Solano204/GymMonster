#FIRST STAGE: Building stage  here I create the JAR with the neccesary files such as pom.xml and mvnw and other and I generate the JAR
FROM openjdk:23-ea-21-jdk as builder
WORKDIR /app/eurecka-service
# copy the pom from a father
COPY ./eurecka/.mvn ./.mvn
COPY ./eurecka/mvnw .
# copy the pom from a eurecka
COPY ./eurecka/pom.xml .

#Download all dependencies only the dependencies and with the run command I DOWNLOAD THEM
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
#RUN ./mvnw dependency:go-offline

#copy code
COPY ./eurecka/src ./src

# Here I generate the jar and skipping the test
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: In this second stages I get the JAR created in the first stage But here I only copy the JAR (I dont copy the rest of the files such as pom.xml and mvnw, this files will be deleted or not included in this stage), and I create the final Docker image , and this image only will have the JAR and will be more lightweight
FROM openjdk:23-ea-21-jdk

WORKDIR /app

# Copy and rename the JAR file from the builder stage
COPY --from=builder /app/eurecka-service/target/eurecka-0.0.1-SNAPSHOT.jar /app/app-eurecka.jar

# Expose the port
EXPOSE 8900

# Set the entry point with the new name
ENTRYPOINT ["java", "-jar", "app-eurecka.jar"]


# docker build --no-cache -t eurecka-server:v1 . -f .\eurecka\Dockerfile
#  docker run -p 8002:80 -d --rm --name msvc-cursos --network spring cursos:v1
