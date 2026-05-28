# Guía de Contribución para StockSync

¡Gracias por tu interés en contribuir a StockSync! Apreciamos mucho tu ayuda para mejorar este proyecto.

Esta guía proporciona las instrucciones sobre cómo reportar errores, sugerir mejoras y enviar código al proyecto.

## Cómo Empezar

Para empezar a contribuir en el proyecto, necesitas configurar tu entorno de desarrollo local.

### Prerrequisitos

- **Backend**: Java 21 o superior, Maven.
- **Frontend**: Node.js (versión 18 o superior), npm.
- **Docker** (opcional, pero recomendado para pruebas completas).

### Configuración Local

1. **Haz un Fork del repositorio** haciendo clic en el botón "Fork" en la parte superior derecha de esta página (si usas GitHub o GitLab).
2. **Clona tu Fork** en tu máquina local:
   ```bash
   git clone https://github.com/TU-USUARIO/StockSync-test.git
   cd StockSync-test
   ```

3. **Configura el Backend (Spring Boot)**:
   ```bash
   cd backend
   ./mvnw clean install
   # Para ejecutar la aplicación:
   ./mvnw spring-boot:run
   ```

4. **Configura el Frontend (Vue 3 + Vuetify)**:
   ```bash
   cd frontend
   npm install
   # Para ejecutar en modo desarrollo:
   npm run dev
   ```

## Flujo de Trabajo para Contribuir

1. **Sincroniza tu Fork** con el repositorio original (upstream) para asegurarte de tener la versión más reciente.
2. **Crea una nueva rama** para tu funcionalidad o corrección de error. Nómbrala de forma descriptiva:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   # o
   git checkout -b bugfix/descripcion-del-error
   ```
3. **Realiza tus cambios** asegurándote de seguir las guías de estilo del proyecto.
4. **Prueba tus cambios** localmente para asegurarte de que todo funciona correctamente y que no rompes nada existente.
5. **Haz un Commit de tus cambios**. Usa mensajes de commit claros y concisos (recomendamos usar Conventional Commits):
   ```bash
   git commit -m "feat: añadir nueva tabla de inventario"
   # o
   git commit -m "fix: corregir error al cargar productos"
   ```
6. **Sube (Push) tu rama** a tu Fork:
   ```bash
   git push origin tu-nueva-rama
   ```
7. **Abre un Pull Request (PR)** en el repositorio original. Proporciona una descripción clara de lo que hace tu PR, por qué es necesario y menciona cualquier Issue relacionado que solucione.

## Guías de Estilo

- **Backend (Java/Spring Boot)**: Sigue las convenciones de estilo de código estándar de Java. Mantén los controladores limpios (manejo de peticiones HTTP) y mueve la lógica de negocio a la capa de Servicios (`@Service`).
- **Frontend (Vue.js)**: Utiliza la *Composition API* con `<script setup>`. Sigue las recomendaciones oficiales de estilo de Vue.js y mantén los componentes pequeños y reutilizables.

## Cómo Reportar Errores (Bugs)

Si encuentras un error, por favor abre un Issue (si tu plataforma lo soporta) y proporciona la siguiente información:

- Una descripción clara del problema.
- Pasos exactos para reproducir el error.
- Comportamiento esperado vs. comportamiento actual.
- Detalles de tu entorno (navegador web, sistema operativo, versión de Node/Java, etc.).
- Capturas de pantalla o logs de error si aplican (ayudan muchísimo).

## Solicitud de Funcionalidades (Feature Requests)

Si tienes una idea para mejorar StockSync, ¡abre un Issue! Explica detalladamente:

- El problema que intentas resolver.
- La solución que propones.
- Alternativas que hayas considerado.

## Código de Conducta

Esperamos que todos los contribuyentes sean respetuosos y considerados con los demás miembros de la comunidad en todas las interacciones. Por favor, mantén un ambiente profesional y colaborativo.