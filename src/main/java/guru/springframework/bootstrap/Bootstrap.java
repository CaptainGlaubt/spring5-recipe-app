package guru.springframework.bootstrap;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Sets;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import guru.springframework.domain.Category;
import guru.springframework.domain.Difficulty;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {
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

	private static final String RECIPE_CHICKEN_DESC = "We have a family motto and it is this: Everything goes better in a tortilla.\r\n"
			+ "\r\n"
			+ "Any and every kind of leftover can go inside a warm tortilla, usually with a healthy dose of pickled jalapenos. I can always sniff out a late-night snacker when the aroma of tortillas heating in a hot pan on the stove comes wafting through the house.\r\n"
			+ "\r\n"
			+ "Today’s tacos are more purposeful – a deliberate meal instead of a secretive midnight snack!\r\n"
			+ "\r\n"
			+ "First, I marinate the chicken briefly in a spicy paste of ancho chile powder, oregano, cumin, and sweet orange juice while the grill is heating. You can also use this time to prepare the taco toppings.\r\n"
			+ "\r\n"
			+ "Grill the chicken, then let it rest while you warm the tortillas. Now you are ready to assemble the tacos and dig in. The whole meal comes together in about 30 minutes!\r\n"
			+ "\r\n"
			+ "Spicy Grilled Chicken TacosThe ancho chiles I use in the marinade are named for their wide shape. They are large, have a deep reddish brown color when dried, and are mild in flavor with just a hint of heat. You can find ancho chile powder at any markets that sell Mexican ingredients, or online.\r\n"
			+ "\r\n"
			+ "I like to put all the toppings in little bowls on a big platter at the center of the table: avocados, radishes, tomatoes, red onions, wedges of lime, and a sour cream sauce. I add arugula, as well – this green isn’t traditional for tacos, but we always seem to have some in the fridge and I think it adds a nice green crunch to the tacos.\r\n"
			+ "\r\n"
			+ "Everyone can grab a warm tortilla from the pile and make their own tacos just they way they like them.\r\n"
			+ "\r\n"
			+ "You could also easily double or even triple this recipe for a larger party. A taco and a cold beer on a warm day? Now that’s living!\r\n";
	private static final String RECIPE_CHICKEN_DIRECTIONS = "1 Prepare a gas or charcoal grill for medium-high, direct heat.\r\n"
			+ "\r\n"
			+ "2 Make the marinade and coat the chicken: In a large bowl, stir together the chili powder, oregano, cumin, sugar, salt, garlic and orange zest. Stir in the orange juice and olive oil to make a loose paste. Add the chicken to the bowl and toss to coat all over.\r\n"
			+ "\r\n" + "Set aside to marinate while the grill heats and you prepare the rest of the toppings.\r\n"
			+ "\r\n"
			+ "3 Grill the chicken: Grill the chicken for 3 to 4 minutes per side, or until a thermometer inserted into the thickest part of the meat registers 165F. Transfer to a plate and rest for 5 minutes.\r\n"
			+ "\r\n"
			+ "4 Warm the tortillas: Place each tortilla on the grill or on a hot, dry skillet over medium-high heat. As soon as you see pockets of the air start to puff up in the tortilla, turn it with tongs and heat for a few seconds on the other side.\r\n"
			+ "\r\n" + "Wrap warmed tortillas in a tea towel to keep them warm until serving.\r\n" + "\r\n"
			+ "5 Assemble the tacos: Slice the chicken into strips. On each tortilla, place a small handful of arugula. Top with chicken slices, sliced avocado, radishes, tomatoes, and onion slices. Drizzle with the thinned sour cream. Serve with lime wedges.\r\n"
			+ "";

	private enum UOM {
		Teaspoon, Tablespoon, Cup, Pinch, Ounce, Piece;
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

	public void bootstrapDb() throws IOException {

		Set<Category> mexicanCategory = new HashSet<>();
		mexicanCategory.add(categoryRepository.findByDescription("Mexican").get());

		// Perfect Guacamole
		Set<Ingredient> guacamoleIngredients = new HashSet<>();
		guacamoleIngredients.add(createIngredient("Ripe avocado", 2, UOM.Piece));
		guacamoleIngredients.add(createIngredient("Kosher salt", 0.5, UOM.Teaspoon));
		guacamoleIngredients.add(createIngredient("Lemon/Lime juice", 1, UOM.Tablespoon));
		guacamoleIngredients.add(createIngredient("Red/green onion", 2, UOM.Tablespoon));
		guacamoleIngredients.add(createIngredient("Serrano chile", 2, UOM.Piece));
		guacamoleIngredients.add(createIngredient("Cilantro", 2, UOM.Tablespoon));
		guacamoleIngredients.add(createIngredient("Tomato", 0.5, UOM.Piece));

		Recipe guacamoleRecipe = new Recipe();
		guacamoleRecipe.setDescription(RECIPE_GUACAMOLE_DESC);
		guacamoleRecipe.setCategories(mexicanCategory);
		guacamoleRecipe.setDifficulty(Difficulty.EASY);
		guacamoleRecipe.setPrepTime(10);
		guacamoleRecipe.setDirections(RECIPE_GUACAMOLE_DIRECTIONS);
		guacamoleRecipe.setImage(getImage("/images/guacamole.jpg"));
		guacamoleRecipe.setIngredients(guacamoleIngredients);

		// Chicken Tacos
		Set<Ingredient> chickenIngredient = Sets.newHashSet();
		chickenIngredient.add(createIngredient("Ancho chili powder", 2, UOM.Tablespoon));
		chickenIngredient.add(createIngredient("Dried oregano", 1, UOM.Teaspoon));
		chickenIngredient.add(createIngredient("Dried cummin", 2, UOM.Teaspoon));
		chickenIngredient.add(createIngredient("Sugar", 1, UOM.Teaspoon));
		chickenIngredient.add(createIngredient("Garlic", 1, UOM.Piece));
		chickenIngredient.add(createIngredient("Orange zest", 1, UOM.Tablespoon));
		chickenIngredient.add(createIngredient("Orange juice", 3, UOM.Tablespoon));
		chickenIngredient.add(createIngredient("Olive oil", 2, UOM.Tablespoon));
		chickenIngredient.add(createIngredient("Chicken thigh", 6, UOM.Piece));
		chickenIngredient.add(createIngredient("Corn tortilla", 6, UOM.Piece));
		chickenIngredient.add(createIngredient("Baby aragula", 3, UOM.Ounce));
		chickenIngredient.add(createIngredient("Medium Ripe avocado", 2, UOM.Piece));
		chickenIngredient.add(createIngredient("Radish", 4, UOM.Piece));
		chickenIngredient.add(createIngredient("Cherry tomato", 0.5, UOM.Piece));
		chickenIngredient.add(createIngredient("Red onion", 0.25, UOM.Piece));
		chickenIngredient.add(createIngredient("Cilantro", 1, UOM.Piece));
		chickenIngredient.add(createIngredient("Sour cream", 0.5, UOM.Cup));
		chickenIngredient.add(createIngredient("Lime", 1, UOM.Piece));

		Recipe chickenRecipe = new Recipe();
		chickenRecipe.setDescription(RECIPE_CHICKEN_DESC);
		chickenRecipe.setCategories(mexicanCategory);
		chickenRecipe.setDifficulty(Difficulty.EASY);
		chickenRecipe.setCookTime(15);
		chickenRecipe.setPrepTime(20);
		chickenRecipe.setDirections(RECIPE_CHICKEN_DIRECTIONS);
		chickenRecipe.setImage(getImage("/images/chicken.jpg"));
		chickenRecipe.setIngredients(chickenIngredient);

		recipeRepository.save(guacamoleRecipe);
		recipeRepository.save(chickenRecipe);
	}

	private Ingredient createIngredient(String description, double amount, UOM uom) {
		Ingredient ingredient = new Ingredient();
		ingredient.setDescription(description);
		ingredient.setAmount(new BigDecimal(amount));
		ingredient.setUom(unitOfMeasureRepository.findByDescription(uom.name()).get());
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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			bootstrapDb();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
