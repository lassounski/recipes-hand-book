package nl.abnamro.cookbook.service;

import jakarta.persistence.EntityManager;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.model.RecipeEntity;
import nl.abnamro.cookbook.model.mapper.RecipeMapper;
import nl.abnamro.cookbook.repository.IngredientRepository;
import nl.abnamro.cookbook.repository.RecipeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeServiceTest {

    private RecipeRepository recipeRepository = mock(RecipeRepository.class);
    private IngredientRepository ingredientRepository = mock(IngredientRepository.class);
    private RecipeMapper recipeMapper = new RecipeMapper();
    private EntityManager entityManager = mock(EntityManager.class);

    private RecipeService recipeService = new RecipeService(recipeRepository, ingredientRepository, recipeMapper, entityManager);

    @Test
    void shouldLookForExistingIngredientsBeforeSavingRecipe() {
        // given
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("recipe name");
        recipeDto.setInstructions("recipe instructions");
        recipeDto.setServings(4);
        recipeDto.setVegetarian(true);
        recipeDto.setIngredients(Set.of("ingredient 1", "ingredient 2"));

        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeDto);
        when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(recipeEntity);
        // when
        recipeService.update(recipeDto);
        // then
        verify(recipeRepository).save(any(RecipeEntity.class));
        verify(ingredientRepository).findByName("ingredient 1");
        verify(ingredientRepository).findByName("ingredient 2");
    }

    @Test
    void shouldIgnoreNullIngredients() {
        // given
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("recipe name");
        recipeDto.setInstructions("recipe instructions");
        recipeDto.setServings(4);
        recipeDto.setVegetarian(true);
        recipeDto.setIngredients(null);

        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeDto);
        when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(recipeEntity);
        // when
        recipeService.update(recipeDto);
        // then
        verify(recipeRepository).save(any(RecipeEntity.class));
    }

    @Test
    void shouldUpdateRecipe() {
        // given
        UUID recipeUUID = UUID.randomUUID();
        RecipeDto recipeDto = mock(RecipeDto.class);
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .id(UUID.randomUUID())
                .name("recipe name")
                .build();
        when(recipeRepository.findById(eq(recipeUUID))).thenReturn(Optional.of(recipeEntity));
        when(recipeRepository.save(eq(recipeEntity))).thenReturn(recipeEntity);
        // when
        recipeService.update(recipeUUID, recipeDto);
        // then
        verify(recipeRepository).findById(eq(recipeUUID));
        verify(recipeRepository).save(any(RecipeEntity.class));
    }

    @Test
    void shouldFindAllRecipes() {
        RecipeEntity recipeEntity = RecipeEntity.builder()
                .id(UUID.randomUUID())
                .name("recipe name")
                .build();
        RecipeEntity recipeEntity2 = RecipeEntity.builder()
                .id(UUID.randomUUID())
                .name("recipe name 2")
                .build();
        when(recipeRepository.findAll()).thenReturn(List.of(recipeEntity, recipeEntity2));

        Set<RecipeDto> recipes = recipeService.findAll();

        verify(recipeRepository).findAll();
        assertThat(recipes).hasSize(2);
        assertThat(recipes)
                .extracting(RecipeDto::getName)
                .containsExactlyInAnyOrder("recipe name", "recipe name 2");
    }
}
