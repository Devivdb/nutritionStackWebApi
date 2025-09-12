# Nutrition Stack Web API

Een RESTful API gebouwd met Spring Boot voor het beheren van voedingsproducten, maaltijden, gebruikersdoelen en dagelijkse tracking. De API gebruikt JWT voor authenticatie en PostgreSQL als primaire database. Ontwikkeld met oog voor eenvoud, DRY, SOLID en onderhoudbaarheid.

### Inhoudsopgave
- Overzicht
- Tech stack
- Vereisten
- Installatie
- Configuratie
- Applicatie starten
- Testen en build
- Authenticatie (JWT)
- Voorbeelden van endpoints
- Postman-collectie

## Overzicht
Deze service biedt endpoints voor:
- Authenticatie en beheer van gebruikers (inloggen/registreren, rollen voor admin)
- Productbeheer (CRUD, bulk upload)
- Maaltijden samenstellen met producten en macro’s
- Dagelijkse voedingstracking
- Rapportages (o.a. dagelijkse/wekelijkse samenvattingen en PDF)

## Tech stack
- Java 17
- Spring Boot 3.5.5
- Spring Web, Security, Data JPA, Validation
- PostgreSQL (runtime), H2 (tests)
- JJWT (JWT), Lombok, iText (PDF)
- Maven (wrapper meegeleverd)

## Vereisten
- Java 17 (JDK)
- Maven (optioneel; `mvnw` wrapper is aanwezig)
- PostgreSQL 14+ lokaal of extern bereikbaar

## Installatie
1) Repository clonen en openen.
2) Zorg dat PostgreSQL draait en er een database bestaat met de waarden uit Configuratie.
3) Dependencies ophalen via Maven wrapper:
```
./mvnw -q dependency:resolve
```

## Configuratie
Standaardconfig staat in `src/main/resources/application.properties`.

Belangrijkste sleutels:
- `spring.datasource.url=jdbc:postgresql://localhost:5432/nutrition-stack-web-api-db`
- `spring.datasource.username=postgres`
- `spring.datasource.password=password`
- `spring.jpa.hibernate.ddl-auto=create-drop` (ontwikkelstand; verwijdert tabellen bij stop/start)
- `spring.sql.init.mode=always` en `data.sql` voor voorbeelddata
- `jwt.secret=...` en `jwt.expiration=86400000` (24h)

Aanbevelingen voor productie:
- Gebruik `spring.jpa.hibernate.ddl-auto=validate` of `update`
- Stel een sterk `jwt.secret` in via omgevingvariabele of extern config
- Schakel `spring.jpa.show-sql` uit

## Applicatie starten
Start in ontwikkelmodus met de Maven wrapper:
```
./mvnw spring-boot:run
```
Standaard draait de API op poort 8080: `http://localhost:8080`.

## Testen en build
- Tests draaien:
```
./mvnw test
```
- Build (fat jar):
```
./mvnw clean package
```
De jar verschijnt onder `target/`.

## Authenticatie (JWT)
De API gebruikt JWT Bearer tokens. Verkrijg een token via login en voeg deze toe als header op vervolgrequests.

- Header: `Authorization: Bearer <JWT_TOKEN>`
- Content-Type: `application/json` (voor requests met body)

## Voorbeelden van endpoints

Onderstaande voorbeelden volgen de voorkeursopmaak: blok met titel, HTTP-methode, URL, Authorization en Content-Type, gevolgd door een apart blok met request body (indien van toepassing). Response bodies zijn samengevat.

### Registreren
```
Titel: Registreren
Methode: POST
URL: /api/auth/register
Authorization: none
Content-Type: application/json
```
Request body:
```json
{
  "username": "jane",
  "password": "SterkWachtwoord123",
  "email": "jane@example.com"
}
```
Respons (200): `AuthResponseDTO { token, user }`

### Inloggen
```
Titel: Inloggen
Methode: POST
URL: /api/auth/login
Authorization: none
Content-Type: application/json
```
Request body:
```json
{
  "username": "jane",
  "password": "SterkWachtwoord123"
}
```
Respons (200): `AuthResponseDTO { token, user }`

### Product aanmaken
```
Titel: Product aanmaken
Methode: POST
URL: /api/products
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
Request body (beknopt):
```json
{
  "name": "Havermout",
  "brand": "X",
  "barcode": "1234567890123",
  "servingSize": 100,
  "macros": { "calories": 380, "protein": 13, "carbs": 67, "fat": 7 }
}
```
Respons (201): `Product` met gegenereerde id.

### Producten ophalen (paginated)
```
Titel: Producten ophalen
Methode: GET
URL: /api/products?page=0&size=20
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
Respons (200): pagina met `Product` items.

### Bulk upload producten
```
Titel: Bulk upload producten
Methode: POST
URL: /api/products/bulk
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
Request body: zie `sample_bulk_upload.json` of `sample_bulk_upload_small.json` in de root.
Respons (200): overzicht van succesvolle en mislukte items.

### Maaltijd aanmaken
```
Titel: Maaltijd aanmaken
Methode: POST
URL: /api/meals
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
Request body (beknopt):
```json
{
  "name": "Ontbijt",
  "products": [
    { "productId": 1, "grams": 80 },
    { "productId": 2, "grams": 200 }
  ]
}
```
Respons (201): `MealResponseDTO` met macro’s.

### Dagelijkse tracking: product loggen
```
Titel: Product loggen
Methode: POST
URL: /api/tracking/logged-products
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
Request body (beknopt):
```json
{
  "productId": 1,
  "date": "2025-09-12",
  "grams": 80
}
```
Respons (201): `LoggedProduct`.

### Rapportage genereren (voorbeeld)
```
Titel: Dagrapport
Methode: GET
URL: /api/nutrition/report/daily?date=2025-09-12
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
Respons (200): `NutritionReportDTO` met totalen en doelvergelijking. PDF endpoint is eveneens beschikbaar.

Opmerking: Raadpleeg de Postman-collectie voor alle beschikbare endpoints en request/response voorbeelden.

## Postman-collectie
In de root staat `Nutrition_Stack_API.postman_collection.json`. Importeer deze in Postman om alle endpoints, omgevingsvariabelen en voorbeeldrequests te gebruiken.

## Ontwikkelnotities
- `data.sql` initialiseert de database met voorbeelddata bij start (door `spring.sql.init.mode=always`).
- In tests wordt H2 gebruikt met `application-test.properties`.
- Rollen en autorisaties zijn geconfigureerd via Spring Security en JWT filters.


