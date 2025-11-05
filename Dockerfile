# 1) Build Stage
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy project source
COPY . .

# Build the jar
RUN ./mvnw clean package -DskipTests

# 2) Run Stage
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/auth-0.0.1-SNAPSHOT.jar app.jar

# Render sets PORT dynamically
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
