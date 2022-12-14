package nl.abnamro.cookbook.service;

import lombok.RequiredArgsConstructor;
import nl.abnamro.cookbook.mapper.RecipeDtoToEntityMapper;
import nl.abnamro.cookbook.mapper.RecipeMapper;
import nl.abnamro.cookbook.model.db.IngredientEntity;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.model.db.RecipeEntity;
import nl.abnamro.cookbook.model.search.SearchRecipeDto;
import nl.abnamro.cookbook.repository.IngredientRepository;
import nl.abnamro.cookbook.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
    private final SearchQueryBuilder searchQueryBuilder;

    public RecipeDto save(RecipeDto recipeDto) {
        RecipeEntity recipeEntity = mapToEntity(recipeDto);
        return recipeMapper.toDto(recipeRepository.save(recipeEntity));
    }

    public Optional<RecipeDto> update(UUID uuid, RecipeDto recipeDto) {
        Optional<RecipeEntity> savedRecipeOptional = recipeRepository.findById(uuid);

        if (savedRecipeOptional.isEmpty()) {
            return Optional.empty();
        }else{
            recipeDto.setId(uuid);
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
                searchQueryBuilder.buildQueryString(searchRecipeDto), RecipeEntity.class);

        searchQueryBuilder.setParameters(query, searchRecipeDto);

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

    public Optional<RecipeDto> findById(UUID id) {
        return recipeRepository.findById(id)
                .map(recipeEntity -> recipeMapper.toDto(recipeEntity));
    }
}
