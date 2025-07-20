FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and config
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x mvnw

# Use wrapper to download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source
COPY . .

# Package the application
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/machikoro-0.0.1-SNAPSHOT.jar"]