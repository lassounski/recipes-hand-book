package nl.abnamro.cookbook.repository;

import nl.abnamro.cookbook.model.RecipeEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends ListCrudRepository<RecipeEntity, UUID> {

    Optional<RecipeEntity> findByName(String name);


}

