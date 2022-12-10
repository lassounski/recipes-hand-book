package nl.abnamro.cookbook.model;

import nl.abnamro.cookbook.mapper.RecipeMapper;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeMapperTest {

    private RecipeMapper recipeMapper = new RecipeMapper();

    @Test
    void shouldMapDtoToEntity() {
        // given
        var recipeDto = new RecipeDto();
        recipeDto.setName("chocolate cake");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(2);
        recipeDto.setInstructions("mix everything and bake it");
        recipeDto.setIngredients(Set.of("chocolate", "flour", "sugar", "eggs", "butter", "milk", "baking powder"));

        // when
        var recipeEntity = recipeMapper.toEntity(recipeDto);

        // then
        assertThat(recipeEntity.getName()).isEqualTo(recipeDto.getName());
        assertThat(recipeEntity.getInstructions()).isEqualTo(recipeDto.getInstructions());
        assertThat(recipeEntity.getServings()).isEqualTo(recipeDto.getServings());
        assertThat(recipeEntity.getVegetarian()).isEqualTo(recipeDto.getVegetarian());
        assertThat(recipeEntity.getIngredients())
                .allMatch(ingredientEntity -> ingredientEntity.getId() == null
                        && StringUtils.isNotBlank(ingredientEntity.getName()));
    }

    @Test
    void shouldMapEntityToDto() {
        // given
        var recipeEntity = RecipeEntity.builder()
                .name("recipe name")
                .instructions("recipe instructions")
                .servings(4)
                .vegetarian(true)
                .ingredients(Set.of(
                        IngredientEntity.builder().name("ingredient 1").build(),
                        IngredientEntity.builder().name("ingredient 2").build()
                ))
                .build();

        // when
        var recipeDto = recipeMapper.toDto(recipeEntity);

        // then
        assertThat(recipeDto.getName()).isEqualTo(recipeEntity.getName());
        assertThat(recipeDto.getInstructions()).isEqualTo(recipeEntity.getInstructions());
        assertThat(recipeDto.getServings()).isEqualTo(recipeEntity.getServings());
        assertThat(recipeDto.getVegetarian()).isEqualTo(recipeEntity.getVegetarian());
        assertThat(recipeDto.getIngredients()).containsExactlyInAnyOrder("ingredient 1", "ingredient 2");
    }

    @Test
    void shouldIgnoreNullIngredientsList() {
        // given
        var recipeDto = new RecipeDto();
        recipeDto.setName("chocolate cake");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(2);
        recipeDto.setInstructions("mix everything and bake it");
        recipeDto.setIngredients(null);

        // when
        var actualRecipeEntity = recipeMapper.toEntity(recipeDto);

        // then
        assertThat(actualRecipeEntity.getIngredients()).isNull();

        // given
        var recipeEntity = RecipeEntity.builder()
                .name("recipe name")
                .instructions("recipe instructions")
                .servings(4)
                .vegetarian(true)
                .ingredients(null)
                .build();

        // when
        var actualRecipeDto = recipeMapper.toDto(recipeEntity);

        // then
        assertThat(actualRecipeDto.getIngredients()).isNull();
    }
}
