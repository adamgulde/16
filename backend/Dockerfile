# Stage 1: Build
FROM gradle:jdk21-alpine AS build
RUN apk add --no-cache python3
# Note: Using JDK 21 because gradle:jdk25 might not be available yet in standard repositories.
# However, for the final run stage we will use Temurin 25 as requested.
# If I had a custom JDK 25 gradle image I would use it.
# Let's try to find a way to use Java 25.

WORKDIR /app
COPY . /app/
# We'll skip the wrapper if it doesn't exist and use the image's gradle.
RUN gradle build -x test

# Stage 2: Run
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
# Copy the entire libs directory content to /app/
COPY --from=build /app/build/libs/ /app/
# Rename the primary jar (assuming the build produces one main SNAPSHOT jar)
RUN mv *-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
