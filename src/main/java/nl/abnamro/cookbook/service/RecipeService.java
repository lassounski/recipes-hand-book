package nl.abnamro.cookbook.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import nl.abnamro.cookbook.model.IngredientEntity;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.model.RecipeEntity;
import nl.abnamro.cookbook.model.SearchRecipeDto;
import nl.abnamro.cookbook.model.mapper.RecipeDtoToEntityMapper;
import nl.abnamro.cookbook.model.mapper.RecipeMapper;
import nl.abnamro.cookbook.repository.IngredientRepository;
import nl.abnamro.cookbook.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;
    private final EntityManager entityManager;

    public RecipeDto update(RecipeDto recipeDto) {
        RecipeEntity recipeEntity = mapToEntity(recipeDto);
        return recipeMapper.toDto(recipeRepository.save(recipeEntity));
    }

    public Optional<RecipeDto> update(UUID uuid, RecipeDto recipeDto) {
        Optional<RecipeEntity> savedRecipeOptional = recipeRepository.findById(uuid);

        if (savedRecipeOptional.isEmpty()) {
            return Optional.empty();
        }else{
            RecipeEntity savedRecipe = savedRecipeOptional.get();
            RecipeDtoToEntityMapper.INSTANCE.updateRecipeFromDto(recipeDto, savedRecipe);
            reconnectIngredients(savedRecipe);
            return Optional.of(recipeMapper.toDto(recipeRepository.save(savedRecipe)));
        }
    }

    private RecipeEntity mapToEntity(RecipeDto recipeDto) {
        RecipeEntity recipeEntity = recipeMapper.toEntity(recipeDto);
        reconnectIngredients(recipeEntity);
        return recipeEntity;
    }

    private void reconnectIngredients(RecipeEntity recipeEntity) {
        if (recipeEntity.getIngredients() != null) {
            recipeEntity.setIngredients(
                    recipeEntity.getIngredients().stream()
                            .map(ingredientEntity -> {
                                Optional<IngredientEntity> ingredientEntityOptional = ingredientRepository.findByName(ingredientEntity.getName());
                                if (ingredientEntityOptional.isPresent()) {
                                    return ingredientEntityOptional.get();
                                }
                                return ingredientEntity;
                            }).collect(Collectors.toSet())
            );
        }
    }

    public List<RecipeDto> searchRecipe(SearchRecipeDto searchRecipeDto) {
        TypedQuery<RecipeEntity> query = entityManager.createQuery(
                "SELECT r FROM RecipeEntity r WHERE r.name = :name ", RecipeEntity.class);

        if (searchRecipeDto.getName() != null) {
            query.setParameter("name", searchRecipeDto.getName());
        }

        List<RecipeEntity> recipes = query.getResultList();

        return recipes.stream()
                .map(recipeEntity -> recipeMapper.toDto(recipeEntity))
                .collect(Collectors.toList());
    }

    public Set<RecipeDto> findAll() {
        return recipeRepository.findAll().stream()
                .map(recipeEntity -> recipeMapper.toDto(recipeEntity))
                .collect(Collectors.toSet());
    }
}
