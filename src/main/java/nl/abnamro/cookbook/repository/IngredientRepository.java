package nl.abnamro.cookbook.repository;


import nl.abnamro.cookbook.model.IngredientEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends ListCrudRepository<IngredientEntity, UUID> {

    Optional<IngredientEntity> findByName(String name);
}
