# Cookbook ABN AMRO application

A Spring Web application that allows users to manage recipes through a REST API.

#### The Database
The application uses a MySQL database to store the recipes. This database was chosen
because of the search-in-text feature that allows searching words in text efficiently. For more elaborate search requirements
ElasticSearch could be used. For better performance, indexes should be used on ingredient name. Since its used to fetch ingredients everytime a recipe is created or updated. Indexes for the recipes name, servings, vegetarian and description should also be used in order to make the searching process more efficient.

#### Pagination
To be more realistic, the getAll recipes should implement pagination. Once the recipe book grows to hundreds it becomes more efficient to retrieve the recipes with pagination.

#### Testing
The integration tests are running against a real database, that makes them slower. To go around that issue, the TestContainer should be started once per IT test session, that could be achieved with a maven plugin attached to the pre-integration-phase.

## Requirements
* Docker
* Java 17

### Running tests
IT tests use TestContainers with a real MySQL database to prevent any incompatible issues while running the application in production.
In the first run of this application MySQL will be downloaded and started in Docker by TestContainers.
Run unit + IT tests:

`$ ./mvnw verify`