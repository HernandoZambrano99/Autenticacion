FROM eclipse-temurin:21-jdk-alpine

# Ajusta si usas wrapper o gradle localmente
WORKDIR /app

# Copia archivos de build para cacheo
COPY build.gradle settings.gradle gradle.* /app/
COPY gradle /app/gradle
# Si es un mono-repo con subprojects, copia el proyecto completo
COPY . /app

# Build: genera el jar
RUN ./gradlew clean bootJar -x test --no-daemon

# Runtime image
FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app

# Copia el JAR construido
COPY --from=build /app/${JAR_FILE} app.jar

# Puerto expuesto (Autenticacion 8080, Solicitudes 8081). Igual puedes dejar 8080 y setear en compose.
EXPOSE 8080

# Variables recomendadas (puedes sobrescribir en docker-compose)
ENV JAVA_OPTS="-Xms256m -Xmx512m -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -jar /app/app.jar" ]
