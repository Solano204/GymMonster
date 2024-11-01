# FIRST STAGE: Building stage
FROM openjdk:23-ea-21-jdk as builder
WORKDIR /app/server-information-service

# Copy main pom and other necessary files
COPY ./server-informations/.mvn ./.mvn
COPY ./server-informations/mvnw .

# Copy the project-specific pom.xml
COPY ./server-informations/pom.xml .

# Download dependencies only
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

# Copy source code
COPY ./server-informations/src ./src

# Build JAR, skipping tests
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: Final image creation
FROM openjdk:23-ea-21-jdk

WORKDIR /app

# Copy and rename the JAR
COPY --from=builder /app/server-information-service/target/server-informations-0.0.1-SNAPSHOT.jar /app/app-server-information.jar

# Expose port (adjust if necessary)
EXPOSE 8111

# Entry point
ENTRYPOINT ["java", "-jar", "app-server-information.jar"]

# docker build --no-cache -t server-information:v1 . -f ./server-information/Dockerfile
