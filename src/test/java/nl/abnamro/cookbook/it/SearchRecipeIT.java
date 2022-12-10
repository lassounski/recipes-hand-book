package nl.abnamro.cookbook.it;

import lombok.SneakyThrows;
import nl.abnamro.cookbook.model.search.SearchItem;
import nl.abnamro.cookbook.model.search.SearchMode;
import nl.abnamro.cookbook.model.search.SearchRecipeDto;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/data.sql"})
@AutoConfigureMockMvc
public class SearchRecipeIT {

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
    void shouldGetRecipeByName() {
        mockMvc.perform(post("/recipes/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SearchRecipeDto.builder()
                                .searchItem(SearchItem.builder()
                                        .field("name")
                                        .value("non existent")
                                        .searchMode(SearchMode.EQUALS)
                                        .build())
                                .build()
                        )))
                .andDo(print())
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/recipes/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SearchRecipeDto.builder()
                                .searchItem(SearchItem.builder()
                                        .field("name")
                                        .value("chicken noodles")
                                        .searchMode(SearchMode.EQUALS)
                                        .build())
                                .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].name").value("chicken noodles"))
                .andExpect(jsonPath("$[0].vegetarian").value(false))
                .andExpect(jsonPath("$[0].servings").value(4));
    }

    @SneakyThrows
    @Test
    void shouldGetVegetarianRecipes() {
        mockMvc.perform(post("/recipes/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SearchRecipeDto.builder()
                                .searchItem(SearchItem.builder()
                                        .field("vegetarian")
                                        .value(false)
                                        .searchMode(SearchMode.EQUALS)
                                        .build())
                                .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        
        mockMvc.perform(post("/recipes/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SearchRecipeDto.builder()
                                .searchItem(SearchItem.builder()
                                        .field("vegetarian")
                                        .value(true)
                                        .searchMode(SearchMode.EQUALS)
                                        .build())
                                .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void shouldGetRecipesThatServe4PeopleWithChicken() {
        mockMvc.perform(post("/recipes/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SearchRecipeDto.builder()
                                .searchItem(SearchItem.builder()
                                        .field("servings")
                                        .value(4)
                                        .searchMode(SearchMode.EQUALS)
                                        .build())
                                .searchItem(SearchItem.builder()
                                        .field("ingredientName")
                                        .value("chicken")
                                        .searchMode(SearchMode.CONTAINS_VALUE_IN_INGREDIENTS)
                                        .build())
                                .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].name").value("chicken noodles"))
                .andExpect(jsonPath("$[0].vegetarian").value(false))
                .andExpect(jsonPath("$[0].servings").value(4));
    }

    @SneakyThrows
    @Test
    void shouldGetRecipesThatDoNotContainChicken() {
        mockMvc.perform(post("/recipes/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SearchRecipeDto.builder()
                                .searchItem(SearchItem.builder()
                                        .field("ingredientName")
                                        .value("chicken")
                                        .searchMode(SearchMode.CONTAINS_VALUE_IN_INGREDIENTS_NOT)
                                        .build())
                                .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].name").value("greek salad"))
                .andExpect(jsonPath("$[0].vegetarian").value(true));
    }
}
