package nl.abnamro.cookbook.service;

import nl.abnamro.cookbook.model.db.IngredientEntity;
import nl.abnamro.cookbook.model.db.RecipeEntity;
import nl.abnamro.cookbook.model.search.SearchItem;
import nl.abnamro.cookbook.model.search.SearchMode;
import nl.abnamro.cookbook.model.search.SearchRecipeDto;
import nl.abnamro.cookbook.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.TypedQuery;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchQueryBuilderTest {

    private IngredientRepository ingredientRepository = mock(IngredientRepository.class);


    private SearchQueryBuilder searchQueryBuilder = new SearchQueryBuilder();

    @BeforeEach
    public void setUp() {
        searchQueryBuilder.setIngredientRepository(ingredientRepository);
    }

    @Test
    void shouldAddMultipleSearchItemsInTheQuery() {
        TypedQuery<RecipeEntity> query = mock(TypedQuery.class);
        SearchRecipeDto searchRecipeDto = SearchRecipeDto.builder()
                .searchItem(SearchItem.builder()
                        .field("name")
                        .value("chicken noodle")
                        .searchMode(SearchMode.EQUALS)
                        .build())
                .searchItem(SearchItem.builder()
                        .field("servings")
                        .value(2)
                        .searchMode(SearchMode.EQUALS)
                        .build())
                .searchItem(SearchItem.builder()
                        .field("vegetarian")
                        .value(true)
                        .searchMode(SearchMode.EQUALS)
                        .build())
                .build();

        String searchQuery = searchQueryBuilder.buildQueryString(searchRecipeDto);
        searchQueryBuilder.setParameters(query, searchRecipeDto);

        assertThat(searchQuery).isEqualTo("SELECT r FROM RecipeEntity r WHERE r.name = :name AND r.servings = :servings AND r.vegetarian = :vegetarian");
        verify(query).setParameter("name", "chicken noodle");
        verify(query).setParameter("servings", 2);
        verify(query).setParameter("vegetarian", true);
    }

    @Test
    void shouldSearchForRecipesContainingChickenAsIngredient() {
        TypedQuery<RecipeEntity> query = mock(TypedQuery.class);
        IngredientEntity ingredientEntity = mock(IngredientEntity.class);

        SearchRecipeDto searchRecipeDto = SearchRecipeDto.builder()
                .searchItem(SearchItem.builder()
                        .field("ingredientName")
                        .value("chicken")
                        .searchMode(SearchMode.CONTAINS_VALUE_IN_INGREDIENTS)
                        .build())
                .build();
        when(ingredientRepository.findByName(eq("chicken")))
                .thenReturn(Optional.of(ingredientEntity));

        String searchQuery = searchQueryBuilder.buildQueryString(searchRecipeDto);
        searchQueryBuilder.setParameters(query, searchRecipeDto);

        assertThat(searchQuery).isEqualTo("SELECT r FROM RecipeEntity r WHERE :ingredientName MEMBER OF r.ingredients");
        verify(query).setParameter("ingredientName", ingredientEntity);
    }

    @Test
    void shouldPerformFullTextSearchOnInstructionsColumn() {
        TypedQuery<RecipeEntity> query = mock(TypedQuery.class);
        SearchRecipeDto searchRecipeDto = SearchRecipeDto.builder()
                .searchItem(SearchItem.builder()
                        .field("instructions")
                        .value("chicken noodle")
                        .searchMode(SearchMode.LIKE)
                        .build())
                .build();

        String searchQuery = searchQueryBuilder.buildQueryString(searchRecipeDto);
        searchQueryBuilder.setParameters(query, searchRecipeDto);

        assertThat(searchQuery).isEqualTo("SELECT r FROM RecipeEntity r WHERE r.instructions LIKE :instructions");
        verify(query).setParameter("instructions", "%chicken noodle%");
    }
}
