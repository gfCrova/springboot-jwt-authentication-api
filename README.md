## Cifrado

Es el proceso de codificar la información de su representación original (texto plano) a texto cifrado, de manera que solamente pueda ser descifrado utilizando una key.

### Algoritmos en Spring Security

* BCrypt
* PBKF2
* scrypt
* argon2

## JWT

Es un estándar abierto que permite transmitir información entre dos partes:

### Funcionamiento JWT

1. Cliente envía una petición a un servidor (/api/login).
2. Servidor valida username y password.
    * Si NO son válidos devolverá una respuesta 401 unauthorized.
    * Si son válidos entonces genera un Token JWT utilizando una secret Key.
3. Servidor devuelve el token JWT generado al Cliente.
4. Cliente envía peticiones a los endpoints del servidor utilizando el token JWT en los headers.
5. Servidor valída el token JWT en cada petición y si es correcto permite el acceso a los datos.

### Ventajas:

* El token se almacena en el Cliente, de manera que consume menos recursos en el Servidor, lo cual permite mejor escalabilidad.

### Desventajas

* El token está en el navegador, no podríamos invalidarlo antes de la fecha de expiración asignada cuando se creó.
* Lo que se realiza es dar la opción de logout, lo cual simplemente borra el token.

## Estructura del Token JWT

3 partes separadas por un punto(.) y codificadas en base 64 cada una:

1. Header

```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

2. Payload (Información, datos del usuario, no sensibles)

```json
{
  "name": "John Doe",
  "admin": true
}
```

3. Asignatura

```
HMACKSHA256(
    base64UrlEncode(header) + "." + base64UrlEncode(payload), secret
```

El User-agent envía el token JWT en los headers:

```
Authorization: Bearer <token>
```

## Configuración Spring

Crear proyecto Spring Boot con:

* Spring Security
* Spring Web
* Spring DevTools
* Spring Data Jpa
* PostgreSQL
* Dependency JWT (se añade manualmente en pom.xml)

```xml
<!-- Source: https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.13.0</version>
    <scope>compile</scope>
</dependency>
```