package nl.abnamro.cookbook.service;

import jakarta.persistence.TypedQuery;
import nl.abnamro.cookbook.model.search.SearchRecipeDto;
import org.springframework.stereotype.Service;

@Service
public class SearchQueryBuilder {
    public String buildQueryString(SearchRecipeDto searchRecipeDto) {
        StringBuilder query = new StringBuilder("SELECT r FROM RecipeEntity r");

        for (int i = 0; i < searchRecipeDto.getSearchItems().size(); i++) {
            if (i == 0) {
                query.append(" WHERE ");
            } else {
                query.append(" AND ");
            }
            query.append("r.")
                    .append(searchRecipeDto.getSearchItems().get(i).getField())
                    .append(" = :").append(searchRecipeDto.getSearchItems().get(i).getField());
        }
        return query.toString();
    }

    public <T> void setParameters(TypedQuery<T> query, SearchRecipeDto searchRecipeDto) {
        searchRecipeDto.getSearchItems().forEach(searchItem -> query.setParameter(searchItem.getField(), searchItem.getValue()));
    }
}
