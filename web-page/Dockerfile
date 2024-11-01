# FIRST STAGE: Building stage
FROM openjdk:23-ea-21-jdk as builder
WORKDIR /app/web-page-service

# Copy main pom and other necessary files
COPY ./web-page/.mvn ./.mvn
COPY ./web-page/mvnw .

# Copy the project-specific pom.xml
COPY ./web-page/pom.xml .

# Download dependencies only
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

# Copy source code
COPY ./web-page/src ./src

# Build JAR, skipping tests
RUN ./mvnw clean package -DskipTests



# SECOND STAGE: Final image creation
FROM openjdk:23-ea-21-jdk

WORKDIR /app

# Copy and rename the JAR
COPY --from=builder /app/web-page-service/target/web-page-0.0.1-SNAPSHOT.jar /app/app-web-page.jar

# Expose port (adjust if necessary)
EXPOSE 8903

# Entry point
ENTRYPOINT ["java", "-jar", "app-web-page.jar"]
