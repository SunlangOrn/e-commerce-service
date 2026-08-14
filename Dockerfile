
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN mkdir -p /app/uploads && \
    addgroup -S appgroup && adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app

USER appuser

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 16800

ENV FILE_UPLOAD_LOCAL_DIR=/app/uploads

ENTRYPOINT ["java", "-jar", "app.jar"]