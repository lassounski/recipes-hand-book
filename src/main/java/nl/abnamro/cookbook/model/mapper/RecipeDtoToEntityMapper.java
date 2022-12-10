package nl.abnamro.cookbook.model.mapper;

import nl.abnamro.cookbook.model.IngredientEntity;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.model.RecipeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecipeDtoToEntityMapper {

    RecipeDtoToEntityMapper INSTANCE = Mappers.getMapper( RecipeDtoToEntityMapper.class );

    void updateRecipeFromDto(RecipeDto dto, @MappingTarget RecipeEntity entity);

    default Set<IngredientEntity> map(Set<String> value){
        return value.stream()
                .map(ingredientName -> IngredientEntity.builder().name(ingredientName).build())
                .collect(Collectors.toSet());
    }
}
