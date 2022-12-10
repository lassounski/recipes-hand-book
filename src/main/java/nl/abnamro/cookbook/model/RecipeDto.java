package nl.abnamro.cookbook.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class RecipeDto {
    private Boolean vegetarian;
    private Integer servings;
    private String name;
    private Set<String> ingredients;
    private String instructions;
}
