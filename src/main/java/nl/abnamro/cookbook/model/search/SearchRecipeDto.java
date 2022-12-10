package nl.abnamro.cookbook.model.search;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class SearchRecipeDto {

    @Singular
    private List<SearchItem> searchItems;
}
