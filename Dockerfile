# BookVerse Multi-Stage Dockerfile
# Optimized for production with security best practices

# Build Stage
FROM maven:3.9.6-eclipse-temurin-25 AS build

# Set build arguments
ARG JAR_FILE=target/bookverse.jar

# Set working directory
WORKDIR /app

# Copy Maven configuration first (for better layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Runtime Stage
FROM eclipse-temurin:25-jre-alpine

# Create non-root user for security
RUN addgroup -g 1000 bookverse && \
    adduser -D -s /bin/sh -u 1000 -G bookverse bookverse

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && \
    chown -R bookverse:bookverse /app

# Copy the built JAR from build stage
ARG JAR_FILE=target/bookverse.jar
COPY --from=build /app/${JAR_FILE} app.jar

# Set proper ownership
RUN chown bookverse:bookverse app.jar

# Switch to non-root user
USER bookverse

# Expose application port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Application entrypoint with proper signal handling
ENTRYPOINT ["dumb-init", "--"]

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]