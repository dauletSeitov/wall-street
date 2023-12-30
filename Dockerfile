# Use the official OpenJDK 17 base image
FROM arm64v8/openjdk:21-jdk-oraclelinux8

# Create an app directory in the container
WORKDIR /app

# Copy the compiled Java application (JAR file) into the app directory
COPY build/libs/street-0.0.1-SNAPSHOT.jar /app/app.jar

# Define a volume to store the user.json file
VOLUME /app/data

# Set the entrypoint or CMD for running your Java application
CMD ["java", "-jar", "/app/app.jar"]