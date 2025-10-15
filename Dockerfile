# ---- Build Stage ----
FROM gradle:8.10-jdk21 AS build
LABEL authors="duy"

WORKDIR /app

# Copy Gradle wrapper và cấu hình trước
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copy phần còn lại
COPY src src

# Cấp quyền thực thi cho gradlew (Linux)
RUN chmod +x gradlew

# Build project và tạo file jar (bỏ qua test)
RUN ./gradlew clean build -x test

# ---- Run Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
