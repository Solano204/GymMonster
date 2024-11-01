# FIRST STAGE: Building stage 
# Here I create the JAR with the necessary files such as pom.xml, mvnw, and other dependencies and generate the JAR
FROM openjdk:23-ea-21-jdk as builder
WORKDIR /app/apigateway-service

# Copy the pom from the main directory
COPY ./gateway/.mvn ./.mvn
COPY ./gateway/mvnw .

# Copy the pom from the apigateway directory
COPY ./gateway/pom.xml .

# Download all dependencies (only the dependencies) with the run command
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# RUN ./mvnw dependency:go-offline

# Copy the source code
COPY ./gateway/src ./src

# Generate the jar, skipping the tests
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: Creating the final Docker image
# Here I only copy the JAR created in the first stage, making the image lightweight.
FROM openjdk:23-ea-21-jdk

WORKDIR /app

# Copy and rename the JAR file from the builder stage
COPY --from=builder /app/apigateway-service/target/gateway-0.0.1-SNAPSHOT.jar /app/app-apigateway.jar

# Expose the port for the API Gateway
EXPOSE 8080

# Set the entry point with the new name
ENTRYPOINT ["java", "-jar", "app-apigateway.jar"]

# docker build --no-cache -t gateway-server:v1 . -f ./gateway/Dockerfile
# docker run -p 8002:80 -d --rm --name msvc-cursos --network spring cursos:v1
