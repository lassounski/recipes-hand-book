package nl.abnamro.cookbook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.abnamro.cookbook.model.RecipeDto;
import nl.abnamro.cookbook.model.SearchRecipeDto;
import nl.abnamro.cookbook.repository.RecipeRepository;
import nl.abnamro.cookbook.service.RecipeService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/recipes",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;

    @GetMapping
    public ResponseEntity recipes() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @PostMapping
    public ResponseEntity saveRecipe(@RequestBody RecipeDto recipeDto) {
        log.debug("Saving recipe {}", recipeDto);
        return ResponseEntity.ok(recipeService.update(recipeDto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity deleteRecipe(@PathVariable String uuid) {
        try {
            recipeRepository.deleteById(UUID.fromString(uuid));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uuid}")
    public ResponseEntity updateRecipe(@PathVariable String uuid, @RequestBody RecipeDto recipeDto) {
        log.debug("Updating recipe {}", recipeDto);
        return ResponseEntity.ok(recipeService.update(UUID.fromString(uuid), recipeDto));
    }

    @PostMapping("/search/")
    public ResponseEntity searchRecipe(@RequestBody SearchRecipeDto searchRecipeDto) {
        log.debug("Search for recipe {}", searchRecipeDto);
        List<RecipeDto> recipes =  recipeService.searchRecipe(searchRecipeDto);
        if(recipes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipes);
    }
}