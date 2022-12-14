package nl.abnamro.cookbook.service;

import nl.abnamro.cookbook.model.db.IngredientEntity;
import nl.abnamro.cookbook.model.search.SearchItem;
import nl.abnamro.cookbook.model.search.SearchMode;
import nl.abnamro.cookbook.model.search.SearchRecipeDto;
import nl.abnamro.cookbook.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import java.util.Map;
import java.util.function.BiFunction;

@Service
public class SearchQueryBuilder {

    private IngredientRepository ingredientRepository;

    @Autowired
    public void setIngredientRepository(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    private final Map<SearchMode, BiFunction<StringBuilder, SearchItem, StringBuilder>> SEARCH_MODE_TO_QUERY = Map.of(
            SearchMode.EQUALS, (builder, searchItem) -> builder.append("r.")
                    .append(searchItem.getField())
                    .append(" = :").append(searchItem.getField()),

            SearchMode.CONTAINS_VALUE_IN_INGREDIENTS, (builder, searchItem) -> builder.append(":")
                    .append(searchItem.getField())
                    .append(" MEMBER OF r.ingredients"),

            SearchMode.CONTAINS_VALUE_IN_INGREDIENTS_NOT, (builder, searchItem) -> builder.append(":")
                    .append(searchItem.getField())
                    .append(" NOT MEMBER OF r.ingredients"),

            SearchMode.LIKE, (builder, searchItem) -> builder.append("r.")
                    .append(searchItem.getField())
                    .append(" LIKE :")
                    .append(searchItem.getField())
    );

    private final BiFunction<TypedQuery, SearchItem, TypedQuery> INGREDIENTS_PARAMETER = (typedQuery, searchItem) -> {
        IngredientEntity ingredient = ingredientRepository.findByName(searchItem.getValue().toString())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        return typedQuery.setParameter(searchItem.getField(), ingredient);
    };

    private final Map<SearchMode, BiFunction<TypedQuery, SearchItem, TypedQuery>> SEARCH_MODE_TO_PARAMETER = Map.of(
            SearchMode.EQUALS, (query, searchItem) -> query.setParameter(searchItem.getField(), searchItem.getValue()),
            SearchMode.LIKE, (query, searchItem) -> query.setParameter(searchItem.getField(), "%"+searchItem.getValue()+"%"),
            SearchMode.CONTAINS_VALUE_IN_INGREDIENTS_NOT, INGREDIENTS_PARAMETER,
            SearchMode.CONTAINS_VALUE_IN_INGREDIENTS, INGREDIENTS_PARAMETER
    );

    public String buildQueryString(SearchRecipeDto searchRecipeDto) {
        StringBuilder query = new StringBuilder("SELECT r FROM RecipeEntity r");

        for (int i = 0; i < searchRecipeDto.getSearchItems().size(); i++) {
            SearchItem searchItem = searchRecipeDto.getSearchItems().get(i);
            if (i == 0) {
                query.append(" WHERE ");
            } else {
                query.append(" AND ");
            }
            SEARCH_MODE_TO_QUERY.get(searchItem.getSearchMode()).apply(query, searchItem);
        }
        return query.toString();
    }

    public <T> void setParameters(TypedQuery<T> query, SearchRecipeDto searchRecipeDto) {
        searchRecipeDto.getSearchItems()
                .forEach(searchItem -> SEARCH_MODE_TO_PARAMETER.get(searchItem.getSearchMode()).apply(query, searchItem));
    }
}
