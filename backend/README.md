# GameZone Backend

Backend REST API para la aplicación GameZone, construido con Spring Boot y Kotlin.

## 🚀 Tecnologías

- **Kotlin** 2.0.21
- **Spring Boot** 3.3.5
- **Spring Data JPA** - Para persistencia de datos
- **H2 Database** - Base de datos en memoria para desarrollo
- **Gradle** - Sistema de construcción

## 📋 Requisitos

- Java 17 o superior
- Gradle 8.x o superior (o usar el wrapper incluido)

## 🔧 Configuración

### Base de Datos

El backend está configurado para usar H2, una base de datos en memoria:

- **URL:** `jdbc:h2:mem:gamezonedb`
- **Usuario:** `sa`
- **Contraseña:** (vacía)
- **Consola H2:** http://localhost:8080/h2-console

### Puerto

El servidor corre en el puerto **8080** por defecto.

## 🏗️ Construcción

```bash
# Desde el directorio backend/
gradle build

# O usando el wrapper del proyecto principal
cd ..
./gradlew :backend:build
```

## ▶️ Ejecución

### Opción 1: Desde el código fuente
```bash
gradle bootRun
```

### Opción 2: Desde el JAR compilado
```bash
java -jar build/libs/gamezone-backend-1.0.0.jar
```

## 📡 API Endpoints

### Usuarios

#### Listar todos los usuarios
```http
GET /api/users
```

#### Registrar nuevo usuario
```http
POST /api/users/register
Content-Type: application/json

{
  "fullName": "Juan Pérez",
  "email": "juan.perez@duoc.cl",
  "password": "Password123!",
  "phone": "+56912345678",
  "generos": ["Acción", "RPG"]
}
```

**Validaciones:**
- Email debe terminar en `@duoc.cl`
- Email debe ser único
- Contraseña debe tener al menos 10 caracteres con mayúscula, minúscula, número y símbolo
- Al menos un género debe ser seleccionado

#### Login
```http
POST /api/users/login
Content-Type: application/json

{
  "email": "juan.perez@duoc.cl",
  "password": "Password123!"
}
```

### Juegos

#### Listar todos los juegos
```http
GET /api/games
```

#### Crear nuevo juego
```http
POST /api/games
Content-Type: application/json

{
  "title": "The Legend of Zelda",
  "genre": "Aventura",
  "price": 59.99,
  "description": "Juego de aventura épico"
}
```

#### Eliminar juego
```http
DELETE /api/games/{id}
```

## 🗄️ Modelo de Datos

### User
```kotlin
- id: Long (auto-generado)
- fullName: String (máx. 100 caracteres)
- email: String (único, máx. 60 caracteres)
- password: String
- phone: String (opcional)
- generos: List<String> (mínimo 1)
```

### Game
```kotlin
- id: Long (auto-generado)
- title: String (máx. 150 caracteres)
- genre: String (máx. 50 caracteres)
- price: Double
- description: String (opcional, máx. 500 caracteres)
```

## 🔍 Consola H2

Para inspeccionar la base de datos durante el desarrollo:

1. Inicia la aplicación
2. Abre http://localhost:8080/h2-console
3. Usa las siguientes credenciales:
   - JDBC URL: `jdbc:h2:mem:gamezonedb`
   - User Name: `sa`
   - Password: (dejar vacío)

## 📝 Logs

Los logs de SQL están habilitados en desarrollo (`spring.jpa.show-sql=true`), lo que permite ver todas las queries ejecutadas en la consola.

## 🔐 Seguridad

**Nota:** Esta es una versión de desarrollo. En producción se debe:
- Usar una base de datos persistente (PostgreSQL, MySQL, etc.)
- Implementar encriptación de contraseñas (BCrypt)
- Agregar autenticación JWT
- Implementar CORS apropiadamente
- Usar HTTPS

## 🧪 Testing

```bash
gradle test
```

## 📦 Estructura del Proyecto

```
backend/
├── src/
│   └── main/
│       ├── kotlin/cl/caa/soto/gamezone/
│       │   ├── BackendApplication.kt      # Punto de entrada
│       │   ├── controller/                # Controladores REST
│       │   │   ├── UserController.kt
│       │   │   └── GameController.kt
│       │   ├── service/                   # Lógica de negocio
│       │   │   ├── UserService.kt
│       │   │   └── GameService.kt
│       │   ├── repository/                # Acceso a datos
│       │   │   ├── UserRepository.kt
│       │   │   └── GameRepository.kt
│       │   └── model/                     # Entidades JPA
│       │       ├── User.kt
│       │       └── Game.kt
│       └── resources/
│           └── application.properties     # Configuración
├── build.gradle.kts                       # Configuración de Gradle
└── settings.gradle.kts                    # Settings de Gradle
```

## 🐛 Troubleshooting

### Error: Puerto 8080 ya en uso
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Error: No se puede conectar a H2
Verifica que la aplicación esté corriendo y que la URL en la consola H2 sea exactamente `jdbc:h2:mem:gamezonedb`.

## 📄 Licencia

Este proyecto es parte del curso de desarrollo en Duoc UC.
