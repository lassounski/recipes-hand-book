# Cookbook ABN AMRO application

A Spring Web application that allows users to manage recipes through a REST API.

#### The Database
The application uses a MySQL database to store the recipes. The search for words in content is executed using the LIKE function.
This could be improved by using the full text search functionality of MySQL.
For more elaborate search requirements ElasticSearch could be used. 
Indexes for the recipes name, servings, vegetarian and description should be used in order to make the searching 
process more efficient.

#### Entity / DTO pattern
Even though in the current implementation the Entity and the DTO are the same, this pattern allows for a better separation of concerns.
For example in case the Entity would have a different structure than the DTO, the Entity could be used for the database operations.

#### Searching
I implemented the search functionality using the following approach:
1. Every search term is composed of three parts: the field name, the operator and the value.
2. The value is obviously the value that we are searching for.
3. The field name is used to determine the column in the database that will be used for the search. In a production environment this shall be whitelisted in the backend to avoid querying all columns.
4. The operator is used to determine the type of search that will be executed. The supported operators are: 
   - `EQUALS` - `r.column = :value`
   - `CONTAINS_VALUE_IN_INGREDIENTS` - `:value MEMBER OF r.ingredients `
   - `CONTAINS_VALUE_IN_INGREDIENTS_NOT` - `:value NOT MEMBER OF r.ingredients `
   - `LIKE` - `r.column LIKE :value` where `:value` is `%:value%`
This approach allows an extensible search functionality. New operators can be added easily and the search operations can be compounded using the `AND` operator.

#### Pagination
To be more realistic, the getAll recipes should implement pagination. Once the recipe book grows to hundreds it becomes more efficient to retrieve the recipes with pagination.

#### Testing
There are unit tests for the project but integration tests make more sense since the application does not have much business logic.
The integration tests are running against a real database using Testcontainers, that makes them slower. 
To go around that issue, the TestContainer should be started once per IT test session, that could be achieved with a maven plugin attached to the pre-integration-phase.
The test coverage is at 98% and that is controlled by the jacoco-maven-plugin.

## Requirements
* Docker
* Java 17

### Running tests
IT tests use TestContainers with a real MySQL database to prevent any incompatible issues while running the application in production.
In the first run of this application MySQL will be downloaded and started in Docker by TestContainers.

Run unit + IT tests:

`$ ./mvnw verify`

### Running the application
#### Build Docker image and start docker compose
`$ docker-compose up -d && docker-compose logs -f`
#### Stopping the application
`$ docker-compose down`

### Using the application
Go to [http://localhost:1234/api/swagger-ui.html](http://localhost:1234/api/swagger-ui.html) to see the API documentation.
Here you can try out the API, there are three recipes that are created by default.
The Swagger UI is not the best for testing the API, I recommend using Postman. 
It is important to set the `Content-Type` header to `application/json` when using Postman.