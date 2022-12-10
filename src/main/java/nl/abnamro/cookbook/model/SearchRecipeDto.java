package nl.abnamro.cookbook.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class SearchRecipeDto {


    /* its better to have a list of search items
    each item should have a value[], field and a search mode
    iterate the list and with each item add the string to query and parameters to the parameter list
    */
    private String name;
    private Integer servings;
    private List<String> withIngredients;
    private List<String> withoutIngredients;
    private String textInInstructions;
}
