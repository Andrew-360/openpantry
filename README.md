# Open Pantry

Run the entire application locally using Docker.
You must have Docker Desktop installed and running before starting the stack.

## Run Instructions
This works on windows, but unfortunately not on mac. If on mac, please use Backup Run Instructions

(This takes a while)
```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- Swagger: http://localhost:8080/swagger-ui/index.html
- H2 Database: http://localhost:8080/h2-console (datasource jdbc:h2:mem:communityPantry)

This setup keeps H2 as the backend database, as it is only a first prototype.


## Backup Run Instructions
If the above does not work, you can run the program normally by following these steps:
1. Navigate to the /communityPantry
2. Run `./gradlew.bat bootrun`
3. Navigate to /fronend
4. Run `npm install`
5. Run `npm start`

## Stop

```bash
docker compose down
```
