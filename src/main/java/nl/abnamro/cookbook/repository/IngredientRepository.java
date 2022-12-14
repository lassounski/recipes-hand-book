package nl.abnamro.cookbook.repository;


import nl.abnamro.cookbook.model.db.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<IngredientEntity, UUID> {

    Optional<IngredientEntity> findByName(String name);
}
