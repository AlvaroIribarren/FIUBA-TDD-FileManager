Para ejecutar el proyecto se debe tener Docker y docker-compose instalado

Ejecutar con el comando 

```
docker-compose up -d
```

Es probable que en la primera ejecucion tarde mucho tiempo (o falle) en ejecutar correctamente, esto se debe a que se esta inicializando el contenedor de la base de datos.

Para parar la ejecucion NO USAR:

```
docker-compose down
```

Esto hace que no se puedan volver a conectar los backend con la base de datos MySQL. Esto es por un problema que tiene el docker de MySQL que cuando se borra el container y se inicia de nuevo los backend cambian de ip dejan de tener permiso para acceder a la base de datos, no por un problema del proyecto.

Para parar la ejecucion utilizar:

```
docker-compose stop
```