# Cookbook ABN AMRO application

A Spring Web application that allows users to manage recipes through a REST API.

The application uses a MySQL database to store the recipes. This database was chosen
because of the search-in-text feature that allows searching words in text efficiently. For more elaborate search requirements
ElasticSearch could be used.

## Requirements
* Docker
* Java 17

### Running tests
IT tests use TestContainers with a real MySQL database to prevent any incompatible issues while running the application in production.
In the first run of this application MySQL will be downloaded and started in Docker by TestContainers.
Run unit + IT tests:

`$ ./mvnw verify`