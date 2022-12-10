DROP TABLE IF EXISTS recipe_ingredient;
DROP TABLE IF EXISTS recipe;
DROP TABLE IF EXISTS ingredient;

CREATE TABLE IF NOT EXISTS recipe (
    recipe_id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    vegetarian BOOLEAN NOT NULL,
    servings INT NOT NULL,
    instructions VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredient (
    ingredient_id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS recipe_ingredient (
    recipe_id BINARY(16) NOT NULL,
    ingredient_id BINARY(16) NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id),
    UNIQUE (recipe_id, ingredient_id)
);