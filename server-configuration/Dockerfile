
# FIRST STAGE: Building stage 
# Here I create the JAR with the necessary files such as pom.xml, mvnw, and other dependencies, and generate the JAR
FROM openjdk:23-ea-21-jdk as builder
WORKDIR /app/server-config-service

COPY ./server-configuration/.mvn ./.mvn
COPY ./server-configuration/mvnw .

# Copy the pom.xml from the server-administrator directory
COPY ./server-configuration/pom.xml .

# Download all dependencies (only dependencies) with the run command
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# RUN ./mvnw dependency:go-offline

# Copy the source code
COPY ./server-configuration/src ./src

# Generate the jar, skipping the tests
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: Creating the final Docker image
# Here I only copy the JAR created in the first stage, making the image lightweight
FROM openjdk:23-ea-21-jdk

WORKDIR /app

# Copy and rename the JAR file from the builder stage
COPY --from=builder /app/server-config-service/target/server-configuration-0.0.1-SNAPSHOT.jar /app/app-server-configuration.jar

# Expose the port for the Server Administrator
EXPOSE 8888

# Set the entry point with the new name
ENTRYPOINT ["java", "-jar", "app-server-configuration.jar"]

# docker build --no-cache -t server-administrator:v1 . -f ./server-administrator/Dockerfile
# docker run -p 8003:80 -d --rm --name msvc-cursos --network spring cursos:v1
