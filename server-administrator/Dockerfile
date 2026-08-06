# FIRST STAGE: Building stage
# Here I create the JAR with the necessary files such as pom.xml, mvnw, and other dependencies, and generate the JAR
FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app/server-administrator-service

# Copy the main pom.xml
COPY ./server-administrator/.mvn ./.mvn
COPY ./server-administrator/mvnw .

# Copy the pom.xml from the server-administrator directory
COPY ./server-administrator/pom.xml .

# Download all dependencies (only dependencies) with the run command
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# RUN ./mvnw dependency:go-offline

# Copy the source code
COPY ./server-administrator/src ./src

# Generate the jar, skipping the tests
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: Creating the final Docker image
# Here I only copy the JAR created in the first stage, making the image lightweight
FROM eclipse-temurin:24-jre

WORKDIR /app

# eclipse-temurin runs as root by default - was never given a non-root user.
RUN groupadd --system spring && useradd --system --gid spring spring

# Copy and rename the JAR file from the builder stage
COPY --from=builder /app/server-administrator-service/target/server-administrator-0.0.1-SNAPSHOT.jar /app/app-server-administrator.jar
RUN chown spring:spring /app/app-server-administrator.jar

USER spring

# Expose the port for the Server Administrator
EXPOSE 8900

# Set the entry point with the new name
ENTRYPOINT ["java", "-jar", "app-server-administrator.jar"]

# docker build --no-cache -t server-administrator:v1 -f ./server-administrator/Dockerfile .

