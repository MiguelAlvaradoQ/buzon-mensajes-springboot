# 📬 Buzón de Mensajes

Sistema web para recibir mensajes de visitantes con almacenamiento en base de datos.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Uso](#uso)
- [Configuración](#configuración)
- [API](#api)
- [Deploy](#deploy)
- [Contribuir](#contribuir)
- [Licencia](#licencia)

## ✨ Características

- ✅ Formulario web para enviar mensajes
- ✅ Almacenamiento en base de datos
- ✅ Panel para visualizar mensajes recibidos
- ✅ Diseño responsive (móvil y desktop)
- ✅ Validación de datos
- ✅ API REST para integraciones

## 🛠️ Tecnologías

**Backend:**
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Hibernate

**Base de Datos:**
- H2 (desarrollo)
- PostgreSQL (producción)

**Frontend:**
- Thymeleaf
- HTML5 / CSS3
- JavaScript

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- [Java JDK 17+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/) (opcional, el proyecto incluye Maven Wrapper)
- [Git](https://git-scm.com/)

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/MiguelAlvaradoQ/buzon-mensajes.git
cd buzon-mensajes
```

### 2. Compilar el proyecto

```bash
# Con Maven instalado
mvn clean install

# Con Maven Wrapper (sin instalación previa)
./mvnw clean install  # Linux/Mac
mvnw.cmd clean install  # Windows
```

### 3. Ejecutar en modo desarrollo

```bash
# Opción 1: Con Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Opción 2: Con Maven Wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Opción 3: Ejecutar el JAR
java -jar target/buzon-mensajes-1.0.0.jar --spring.profiles.active=dev
```

### 4. Abrir en el navegador

```
http://localhost:8080
```

## 💻 Uso

### Enviar un mensaje

1. Abre `http://localhost:8080`
2. Completa el formulario:
    - Nombre (mínimo 2 caracteres)
    - Email (debe ser válido)
    - Mensaje (mínimo 10 caracteres)
3. Haz clic en "Enviar"

### Ver mensajes recibidos

1. Abre `http://localhost:8080/mensajes`
2. Verás la lista de todos los mensajes

### Consola H2 (solo en desarrollo)

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:buzon
User: sa
Password: (dejar vacío)
```

## ⚙️ Configuración

### Perfiles de Spring

El proyecto usa múltiples perfiles:

**Desarrollo (`dev`):**
```bash
java -jar app.jar --spring.profiles.active=dev
```
- Base de datos H2 en memoria
- Logs detallados
- Hot reload habilitado

**Producción (`prod`):**
```bash
java -jar app.jar --spring.profiles.active=prod
```
- PostgreSQL
- Logs mínimos
- Optimizaciones de performance

### Variables de Entorno

Para producción, configura:

```bash
export DATABASE_URL=jdbc:postgresql://host:5432/database
export DB_USERNAME=usuario
export DB_PASSWORD=contraseña
```

## 📡 API

### Endpoints disponibles

#### Obtener todos los mensajes
```http
GET /api/mensajes
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "contenido": "Hola, me interesa el proyecto",
    "fechaCreacion": "2025-10-12T21:05:33"
  }
]
```

#### Crear un mensaje
```http
POST /api/mensajes
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "contenido": "Este es mi mensaje"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "contenido": "Este es mi mensaje",
  "fechaCreacion": "2025-10-12T21:05:33"
}
```

#### Marcar mensaje como leído
```http
PUT /api/mensajes/{id}/leido
```

### Manejo de Errores

Todos los errores siguen este formato:

```json
{
  "timestamp": "2025-10-12T21:05:33",
  "status": 404,
  "error": "Not Found",
  "message": "Mensaje no encontrado",
  "path": "/api/mensajes/999"
}
```

## 🚢 Deploy

### Railway.app

1. Crea cuenta en [Railway.app](https://railway.app)
2. Conecta tu repositorio de GitHub
3. Railway detecta automáticamente Spring Boot
4. Agrega PostgreSQL desde el dashboard
5. Configura variables de entorno
6. Deploy automático ✨

### Render.com

1. Crea cuenta en [Render.com](https://render.com)
2. New → Web Service
3. Conecta repositorio
4. Build Command: `./mvnw clean install`
5. Start Command: `java -jar target/buzon-mensajes-1.0.0.jar --spring.profiles.active=prod`
6. Agrega PostgreSQL database
7. Deploy

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea tu rama (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guía de Estilo

- Usa nombres descriptivos en español
- Sigue convenciones de Java (camelCase para variables, PascalCase para clases)
- Documenta métodos públicos con Javadoc
- Escribe tests para nuevas funcionalidades

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

## 👤 Autor

**Miguel Alvarado**

- GitHub: [@MiguelAlvaradoQ](https://github.com/MiguelAlvaradoQ)
- Email: miguelf.alv.q.28@gmail.com

## 🙏 Agradecimientos

- Spring Boot Documentation
- Thymeleaf Documentation
- Comunidad de Stack Overflow

---

⭐ Si este proyecto te fue útil, considera darle una estrella en Git