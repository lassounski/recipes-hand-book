package nl.abnamro.cookbook.model.search;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class SearchItem {

    private String field;
    private Object value;
    private SearchMode searchMode;
}
