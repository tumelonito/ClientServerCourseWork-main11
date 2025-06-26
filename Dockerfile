# ----------- Build Stage -----------
FROM maven:3.9.6-eclipse-temurin-22 AS builder

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# ----------- Run Stage -----------
FROM eclipse-temurin:22-jdk

WORKDIR /app

COPY --from=builder /app/target/CourseWork-1.0-SNAPSHOT.jar app.jar
COPY .env .env

ENTRYPOINT ["sh", "-c", "\
  while ! timeout 1 bash -c 'echo > /dev/tcp/$DB_HOST/3306'; do \
    echo '⏳ Waiting for MySQL...'; sleep 2; \
  done; \
  echo '✅ MySQL is up - starting app'; \
  java -jar app.jar"]
