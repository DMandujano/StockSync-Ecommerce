# Etapa 1: Build del Frontend (Vue)
# Usa una imagen de Node.js para construir el frontend
FROM node:18-alpine AS frontend-build
WORKDIR /app

# Copia solo los archivos de manifiesto para aprovechar la caché de Docker
COPY frontend/package.json frontend/package-lock.json ./frontend/
# Instala las dependencias del frontend
RUN npm install --prefix frontend

# Copia el resto del código fuente del frontend
COPY frontend/ ./frontend/
# Construye la aplicación de Vue para producción
RUN npm run build --prefix frontend

# Etapa 2: Build del Backend (Spring Boot)
# Usa una imagen de Java (Temurin es una excelente opción)
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app

# Copia solo los archivos de manifiesto de Maven para cachear las dependencias
COPY backend/pom.xml backend/mvnw ./backend/
COPY backend/.mvn ./backend/.mvn
# Da permisos de ejecución al Maven Wrapper y descarga dependencias
RUN chmod +x ./backend/mvnw && ./backend/mvnw dependency:go-offline -f ./backend/pom.xml

# Copia el resto del código fuente del backend
COPY backend/ ./backend/

# Copia los archivos compilados del frontend desde la etapa anterior
COPY --from=frontend-build /app/frontend/dist ./backend/src/main/resources/static

# Empaqueta la aplicación de Spring Boot sin ejecutar los tests
RUN ./backend/mvnw package -f ./backend/pom.xml -DskipTests

# Etapa 3: Imagen Final de Producción
# Usa una imagen base de Java ligera para la ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia el archivo .jar final desde la etapa de construcción del backend
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Expone el puerto en el que correrá la aplicación
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]