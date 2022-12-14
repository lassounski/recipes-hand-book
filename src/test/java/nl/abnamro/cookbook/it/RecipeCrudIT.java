package nl.abnamro.cookbook.it;

import lombok.SneakyThrows;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.repository.IngredientRepository;
import nl.abnamro.cookbook.repository.RecipeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/data.sql"})
@AutoConfigureMockMvc
class RecipeCrudIT {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    private static final MySQLContainer mysql = new MySQLContainer("mysql:latest")
            .withDatabaseName("cookbook_db");
    @BeforeAll
    static void setup() {
        mysql.withReuse(true);
        mysql.start();
    }

    @AfterAll
    static void tearDown() {
        mysql.stop();
    }

    @DynamicPropertySource
    public static void overrideDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver.driver-class-name", mysql::getDriverClassName);
    }

    @SneakyThrows
    @Test
    void shouldGetAllRecipes() {
        var response = mockMvc.perform(get("/recipes").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var actualRecipe = Arrays.asList(objectMapper.readValue(response, RecipeDto[].class));

        assertThat(actualRecipe).hasSizeGreaterThan(2);
    }

    @SneakyThrows
    @Test
    void shouldFindById() {
        mockMvc.perform(get("/recipes/d4e2917a-77b7-11ed-a1eb-0242ac120002").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("fried chicken"));
    }

    @SneakyThrows
    @Test
    void shouldSaveRecipeAndNewIngredients() {
        var recipeDto = new RecipeDto();
        recipeDto.setName("chocolate cake");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(2);
        recipeDto.setInstructions("mix everything and bake it");
        //butter is already present in the database
        recipeDto.setIngredients(Set.of("chocolate", "flour", "sugar", "eggs", "butter", "milk", "baking powder"));

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andDo(print())
                .andExpect(jsonPath("$.name").value("chocolate cake"))
                .andExpect(jsonPath("$.vegetarian").value(true))
                .andExpect(jsonPath("$.servings").value(2))
                .andExpect(jsonPath("$.instructions").value("mix everything and bake it"))
                .andExpect(jsonPath("$.ingredients").value(containsInAnyOrder("chocolate", "flour", "sugar", "eggs", "butter", "milk", "baking powder")))
                .andExpect(status().isOk());

        assertThat(ingredientRepository.findAll()).hasSizeGreaterThan(7);
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/data.sql"})
    void shouldDeleteRecipeButNotTheIngredientsIfOtherRecipeIsUsingThem() {
        mockMvc.perform(delete("/recipes/d4e49174-77b7-11ed-a1eb-0242ac120002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(recipeRepository.findById(UUID.fromString("d4e49174-77b7-11ed-a1eb-0242ac120002"))).isNotPresent();
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/data.sql"})
    void shouldUpdateRecipeAndItsIngredients() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("delicious chocolate cake");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(2);
        recipeDto.setInstructions("mix everything and bake it");
        recipeDto.setIngredients(Set.of("chocolate", "flour", "brown sugar", "eggs", "butter", "milk", "baking powder"));

        mockMvc.perform(put("/recipes/d4e2917a-77b7-11ed-a1eb-0242ac120002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("d4e2917a-77b7-11ed-a1eb-0242ac120002"))
                .andExpect(jsonPath("$.name").value("delicious chocolate cake"))
                .andExpect(jsonPath("$.vegetarian").value(true))
                .andExpect(jsonPath("$.servings").value(2))
                .andExpect(jsonPath("$.instructions").value("mix everything and bake it"))
                .andExpect(jsonPath("$.ingredients").value(containsInAnyOrder("chocolate", "flour", "brown sugar", "eggs", "butter", "milk", "baking powder")))
                .andExpect(status().isOk());
    }
}
