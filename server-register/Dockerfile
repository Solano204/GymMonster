# FIRST STAGE: Building stage
FROM eclipse-temurin:25-jdk AS builder
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
FROM eclipse-temurin:25-jre

WORKDIR /app

# eclipse-temurin runs as root by default - was never given a non-root user.
RUN groupadd --system spring && useradd --system --gid spring spring

# Copy and rename the JAR
COPY --from=builder /app/server-register-service/target/server-register-0.0.1-SNAPSHOT.jar /app/app-server-register.jar
RUN chown spring:spring /app/app-server-register.jar

USER spring

# Expose port (adjust if necessary)
EXPOSE 8902

# Entry point
ENTRYPOINT ["java", "-jar", "app-server-register.jar"]

# docker build --no-cache -t server-register:v1 -f ./server-register/Dockerfile .
