package nl.abnamro.cookbook.service;

import jakarta.persistence.TypedQuery;
import nl.abnamro.cookbook.model.RecipeEntity;
import nl.abnamro.cookbook.model.search.SearchItem;
import nl.abnamro.cookbook.model.search.SearchMode;
import nl.abnamro.cookbook.model.search.SearchRecipeDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SearchQueryBuilderTest {

    private SearchQueryBuilder searchQueryBuilder = new SearchQueryBuilder();

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

}
