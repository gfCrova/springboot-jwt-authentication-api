# 🔐 Spring Boot JWT Authentication API

API REST desarrollada con **Spring Boot** que implementa **autenticación y autorización usando JWT (JSON Web Token)** mediante **Spring Security**.

El proyecto demuestra cómo construir un sistema de seguridad **stateless**, donde el cliente se autentica una vez y luego utiliza un **token JWT** para acceder a los endpoints protegidos.

Este proyecto fue desarrollado como práctica para comprender:

* Spring Security
* JWT Authentication
* Arquitectura REST
* Configuración moderna de seguridad en Spring Boot

---

# 🚀 Tecnologías utilizadas

* Java 17+
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT (JSON Web Token)
* Maven
* Swagger / OpenAPI (documentación de endpoints)
* BCrypt (hash de contraseñas)

---

# 🧠 Conceptos implementados

Este proyecto implementa los siguientes conceptos clave de seguridad en aplicaciones backend:

* Autenticación basada en **JWT**
* Seguridad **stateless**
* Filtros personalizados en Spring Security
* Protección de endpoints
* Manejo de errores de autenticación
* Hash seguro de contraseñas con **BCrypt**
* Registro y login de usuarios
* Configuración de **CORS**

---

# 🏗️ Arquitectura del proyecto

La estructura del proyecto sigue una arquitectura común en aplicaciones **Spring Boot REST**.

```text
src/main/java
│
├── config
│   ├── SecurityConfig
│   ├── JwtRequestFilter
│   ├── JwtAuthEntryPoint
│   └── JwtTokenUtil
│
├── controller
│   └── AuthController
│
├── repository
│   └── UserRepository
│
├── service
│   └── UserDetailsServiceImpl
│
├── domain
│   └── User
│
└── payload
    ├── LoginRequest
    ├── RegisterRequest
    ├── JwtResponse
    └── MessageResponse
```

---

# 🔐 Flujo de autenticación

El proceso de autenticación funciona de la siguiente manera:

```text
CLIENT
  │
  │ POST /api/auth/login
  │ username + password
  ▼
AuthenticationManager
  │
  ▼
UserDetailsServiceImpl
  │
  ▼
Usuario encontrado en base de datos
  │
  ▼
JwtTokenUtil genera JWT
  │
  ▼
Cliente recibe TOKEN
```

Luego en cada request:

```text
CLIENT
  │
  │ Authorization: Bearer <JWT>
  ▼
JwtRequestFilter
  │
  ▼
Validación del token
  │
  ▼
SecurityContextHolder
  │
  ▼
Endpoint protegido
```

Si el token es inválido:

```text
JwtAuthEntryPoint
        │
        ▼
HTTP 401 Unauthorized
```

---

# ⚙️ Componentes de seguridad

## 1️⃣ SecurityConfig

Clase que configura la seguridad global de la aplicación.

Responsabilidades:

* Definir `SecurityFilterChain`
* Registrar el filtro JWT
* Configurar CORS
* Definir endpoints públicos
* Configurar `AuthenticationProvider`
* Definir `PasswordEncoder`
* Establecer política **STATELESS**

Ejemplo de configuración de endpoints:

```java
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers("/swagger*/**").permitAll()
.anyRequest().authenticated();
```

---

## 2️⃣ JwtTokenUtil

Clase encargada de **crear y validar tokens JWT**.

Funciones principales:

### Generar Token

Se crea cuando el login es exitoso.

El token incluye:

* username
* fecha de creación
* fecha de expiración

---

### Obtener username del token

Permite identificar al usuario dentro del JWT.

---

### Validar token

Verifica:

* firma
* expiración
* formato válido

---

## 3️⃣ JwtRequestFilter

Filtro que intercepta **todas las requests HTTP**.

Responsabilidades:

1. Leer el header `Authorization`
2. Extraer el JWT
3. Validar el token
4. Obtener el username
5. Cargar el usuario desde la base de datos
6. Crear objeto `Authentication`
7. Guardar autenticación en `SecurityContext`

Header esperado:

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## 4️⃣ JwtAuthEntryPoint

Maneja los errores cuando un usuario intenta acceder a un recurso protegido sin autenticación válida.

Devuelve:

```http
401 Unauthorized
```

---

# 👤 AuthController

Controlador encargado de registrar usuarios y autenticar.

Ruta base:

```
/api/auth
```

---

## 🔑 Login

Endpoint:

```
POST /api/auth/login
```

Request:

```json
{
  "username": "user",
  "password": "1234"
}
```

Proceso:

1. Se autentica el usuario con `AuthenticationManager`
2. Si es válido se genera un JWT
3. Se devuelve el token

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

## 📝 Register

Endpoint:

```
POST /api/auth/register
```

Request:

```json
{
  "username": "user",
  "email": "user@email.com",
  "password": "1234"
}
```

Proceso:

1. Validar que el username no exista
2. Validar que el email no exista
3. Encriptar contraseña
4. Guardar usuario

Respuesta:

```json
{
  "message": "Register Successfully!"
}
```

---

# 🗄️ Configuración de base de datos

En `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Esto indica que **Hibernate actualizará el esquema de la base de datos automáticamente según las entidades**.

Opciones comunes:

| Valor       | Comportamiento           |
| ----------- | ------------------------ |
| create      | recrea tablas            |
| update      | actualiza esquema        |
| create-drop | crea y elimina al cerrar |
| validate    | valida estructura        |
| none        | no modifica DB           |

---

# 🔑 Seguridad con JWT

Los JWT tienen tres partes:

```
HEADER.PAYLOAD.SIGNATURE
```

Ejemplo:

```
eyJhbGciOiJIUzUxMiJ9
.
eyJzdWIiOiJ1c2VyIn0
.
ASDFGASDFGASDFG
```

---

# 🧪 Ejemplo de request autenticado

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

# 📚 Endpoints principales

| Método | Endpoint           | Descripción         |
| ------ | ------------------ | ------------------- |
| POST   | /api/auth/login    | Login de usuario    |
| POST   | /api/auth/register | Registro de usuario |

---

Proyecto realizado como práctica para aprender:

* Spring Boot
* Spring Security
* JWT
* APIs REST seguras

---

# 📄 Licencia

Este proyecto es solo para fines educativos.
