package guru.springframework.bootstrap;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.math.BigDecimal;

import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Sets;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import guru.springframework.domain.Category;
import guru.springframework.domain.Difficulty;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;

@Configuration
public class Bootstrap {
	private static final String RECIPE_GUACAMOLE_DESC = "Guacamole is so easy. All you really need to make guacamole is ripe avocados and salt. After that, a little lime or lemon juice—a splash of acidity— will help to balance the richness of the avocado. Then if you want, add chopped cilantro, chiles, onion, and/or tomato.\r\n"
			+ "\r\n"
			+ "Once you have basic guacamole down, you may experiment with variations including strawberries, pineapple, mangoes, even watermelon. You can get creative with homemade guacamole!";

	private static final String RECIPE_GUACAMOLE_DIRECTIONS = "1 Cut avocado, remove flesh: Cut the avocados in half. Remove seed. Score the inside of the avocado with a blunt knife and scoop out the flesh with a spoon. (See How to Cut and Peel an Avocado.) Place in a bowl.\r\n"
			+ "\r\n"
			+ "2 Mash with a fork: Using a fork, roughly mash the avocado. (Don't overdo it! The guacamole should be a little chunky.)\r\n"
			+ "\r\n"
			+ "3 Add salt, lime juice, and the rest: Sprinkle with salt and lime (or lemon) juice. The acid in the lime juice will provide some balance to the richness of the avocado and will help delay the avocados from turning brown.\r\n"
			+ "\r\n"
			+ "Add the chopped onion, cilantro, black pepper, and chiles. Chili peppers vary individually in their hotness. So, start with a half of one chili pepper and add to the guacamole to your desired degree of hotness.\r\n"
			+ "\r\n"
			+ "Remember that much of this is done to taste because of the variability in the fresh ingredients. Start with this recipe and adjust to your taste.\r\n"
			+ "\r\n"
			+ "4 Cover with plastic and chill to store: Place plastic wrap on the surface of the guacamole cover it and to prevent air reaching it. (The oxygen in the air causes oxidation which will turn the guacamole brown.) Refrigerate until ready to serve.\r\n"
			+ "\r\n"
			+ "Chilling tomatoes hurts their flavor, so if you want to add chopped tomato to your guacamole, add it just before serving.";

	// INSERT INTO category (description) VALUES ('American');
	// INSERT INTO category (description) VALUES ('Italian');
	// INSERT INTO category (description) VALUES ('Mexican');
	// INSERT INTO category (description) VALUES ('Fast Food');
	// INSERT INTO unit_of_measure (description) VALUES ('Teaspoon');
	// INSERT INTO unit_of_measure (description) VALUES ('Tablespoon');
	// INSERT INTO unit_of_measure (description) VALUES ('Cup');
	// INSERT INTO unit_of_measure (description) VALUES ('Pinch');
	// INSERT INTO unit_of_measure (description) VALUES ('Ounce');

	private enum UOM {
		Teaspoon, Tablespoon, Cup, Pinch, Ounce;
	}

	private CategoryRepository categoryRepository;
	private RecipeRepository recipeRepository;
	private UnitOfMeasureRepository unitOfMeasureRepository;

	public Bootstrap(CategoryRepository categoryRepository, RecipeRepository recipeRepository,
			UnitOfMeasureRepository unitOfMeasureRepository) {
		this.categoryRepository = categoryRepository;
		this.recipeRepository = recipeRepository;
		this.unitOfMeasureRepository = unitOfMeasureRepository;
	}

	@PostConstruct
	public void bootstrapDb() throws IOException {
		// Perfect Guacamole
		Set<Category> guacamoleCategories = Sets.newHashSet();
		guacamoleCategories.add(categoryRepository.findByDescription("Mexican").get());

		Set<Ingredient> ingredients = Sets.newHashSet();
		ingredients.add(createIngredient("Ripe avocados", 2, null));
		ingredients.add(createIngredient("Kosher salt", 0.5, UOM.Teaspoon));
		ingredients.add(createIngredient("Lemon/Lime juice", 1, UOM.Tablespoon));
		ingredients.add(createIngredient("Red/green onion", 2, UOM.Tablespoon));
		ingredients.add(createIngredient("Serrano chile", 2, null));
		ingredients.add(createIngredient("Cilantro", 2, UOM.Tablespoon));
		ingredients.add(createIngredient("Tomato", 0.5, null));

		Recipe recipe = new Recipe();
		recipe.setDescription(RECIPE_GUACAMOLE_DESC);
		recipe.setCategories(guacamoleCategories);
		recipe.setDifficulty(Difficulty.EASY);
		recipe.setPrepTime(10);
		recipe.setCookTime(10);
		recipe.setDirections(RECIPE_GUACAMOLE_DIRECTIONS);
		recipe.setImage(getImage("/images/guacamole.jpg"));
		recipe.setIngredients(ingredients);
	}

	private Ingredient createIngredient(String description, double amount, UOM uom) {
		Ingredient ingredient = new Ingredient();
		ingredient.setDescription(description);
		ingredient.setAmount(new BigDecimal(amount));
		if (uom != null) {
			ingredient.setUom(unitOfMeasureRepository.findByDescription(uom.name()).get());
		}
		return ingredient;
	}

	private Byte[] getImage(String path) throws IOException {
		Resource imgResource = new ClassPathResource(path);
		File imgFile = imgResource.getFile();
		FileInputStream fileIn = new FileInputStream(imgFile);
		byte[] bytes = IOUtils.toByteArray(fileIn);
		Byte[] imgBytes = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			imgBytes[i] = bytes[i];
		}
		return imgBytes;
	}
}
