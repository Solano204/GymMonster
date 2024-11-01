# FIRST STAGE: Building stage
FROM openjdk:23-ea-21-jdk as builder
WORKDIR /app/server-register-service

# Copy main pom and other necessary files
COPY ./server-register/.mvn ./.mvn
COPY ./server-register/mvnw .

# Copy the project-specific pom.xml
COPY ./server-register/pom.xml .

# Download dependencies only
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

# Copy source code
COPY ./server-register/src ./src

# Build JAR, skipping tests
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: Final image creation
FROM openjdk:23-ea-21-jdk

WORKDIR /app

# Copy and rename the JAR
COPY --from=builder /app/server-register-service/target/server-register-0.0.1-SNAPSHOT.jar /app/app-server-register.jar

# Expose port (adjust if necessary)
EXPOSE 8902

# Entry point
ENTRYPOINT ["java", "-jar", "app-server-register.jar"]

# docker build --no-cache -t server-register:v1 . -f ./server-register/Dockerfile
