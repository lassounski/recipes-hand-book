INSERT INTO recipe(recipe_id, name, vegetarian, servings, instructions)
VALUES (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), 'fried chicken', FALSE, 6, 'cut the chicken into pieces, fry it, and eat it'),
       (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), 'chicken noodles', FALSE, 4, 'cut the chicken into pieces, fry it, boild the noodeles, mix it all up and eat it');

INSERT INTO ingredient(ingredient_id, name)
values (unhex(replace('23ef78b4-77b8-11ed-a1eb-0242ac120002', '-', '')), 'chicken'),
       (unhex(replace('fbb29d72-77b7-11ed-a1eb-0242ac120002', '-', '')), 'oil'),
       (unhex(replace('2c9442c4-77b8-11ed-a1eb-0242ac120002', '-', '')), 'salt'),
       (unhex(replace('372e0152-77b8-11ed-a1eb-0242ac120002', '-', '')), 'pepper'),
       (unhex(replace('3b94de82-77b8-11ed-a1eb-0242ac120002', '-', '')), 'flour'),
       (unhex(replace('3eae1534-77b8-11ed-a1eb-0242ac120002', '-', '')), 'water'),
       (unhex(replace('41f236bc-77b8-11ed-a1eb-0242ac120002', '-', '')), 'butter'),
       (unhex(replace('44509570-77b8-11ed-a1eb-0242ac120002', '-', '')), 'garlic'),
       (unhex(replace('34519570-77b8-11ed-a1eb-0242ac120002', '-', '')), 'noodles'),
       (unhex(replace('478cb8b8-77b8-11ed-a1eb-0242ac120002', '-', '')), 'onion')
;

INSERT INTO recipe_ingredient(recipe_id, ingredient_id)
VALUES (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('23ef78b4-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('34519570-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('372e0152-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('3b94de82-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('3eae1534-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e49174-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('41f236bc-77b8-11ed-a1eb-0242ac120002', '-', ''))),

       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('23ef78b4-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('fbb29d72-77b7-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('2c9442c4-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('372e0152-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('3b94de82-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('3eae1534-77b8-11ed-a1eb-0242ac120002', '-', ''))),
       (unhex(replace('d4e2917a-77b7-11ed-a1eb-0242ac120002', '-', '')), unhex(replace('41f236bc-77b8-11ed-a1eb-0242ac120002', '-', '')))
;

