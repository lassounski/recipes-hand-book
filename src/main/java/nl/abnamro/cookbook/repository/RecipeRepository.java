package nl.abnamro.cookbook.repository;

import nl.abnamro.cookbook.model.db.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<RecipeEntity, UUID> {
}

