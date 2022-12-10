package nl.abnamro.cookbook.model.mapper;

import nl.abnamro.cookbook.model.IngredientEntity;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.model.RecipeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RecipeMapper {
    public RecipeEntity toEntity(RecipeDto recipeDto) {
        RecipeEntity recipeEntity = new ModelMapper().map(recipeDto, RecipeEntity.class);
        if (recipeDto.getIngredients() != null) {
            recipeEntity.setIngredients(
                    recipeDto.getIngredients().stream()
                            .map(ingredientName -> IngredientEntity.builder().name(ingredientName).build())
                            .collect(Collectors.toSet())
            );
        } //else set to null
        return recipeEntity;
    }

    public RecipeDto toDto(RecipeEntity recipeEntity) {
        RecipeDto recipeDto = new ModelMapper().map(recipeEntity, RecipeDto.class);
        if (recipeDto.getIngredients() != null) {
            recipeDto.setIngredients(recipeEntity.getIngredients().stream()
                    .map(ingredientEntity -> ingredientEntity.getName())
                    .collect(Collectors.toSet()));
        }
        return recipeDto;
    }
}
