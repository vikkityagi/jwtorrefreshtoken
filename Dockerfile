# 1) Build Stage
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy everything
COPY . .

# ✅ Make the Maven wrapper executable
RUN chmod +x mvnw

# ✅ Build the JAR using Maven Wrapper
RUN ./mvnw clean package -DskipTests

# 2) Run Stage
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/auth-0.0.1-SNAPSHOT.jar app.jar

# Render uses a dynamic port
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
