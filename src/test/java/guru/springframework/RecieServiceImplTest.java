package guru.springframework;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.services.RecipeServiceImpl;

public class RecieServiceImplTest {
	private RecipeServiceImpl recipeService;
	@Mock
	private RecipeRepository recipeRepository;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		recipeService = new RecipeServiceImpl(recipeRepository);
	}

	@Test
	public void getRecipes() throws Exception {
		Recipe recipeMock = new Recipe();
		HashSet<Recipe> recipesMock = new HashSet<>();
		recipesMock.add(recipeMock);
		when(recipeService.getRecipes()).thenReturn(recipesMock);
		Set<Recipe> recipes = recipeService.getRecipes();
		assertEquals(recipes.size(), 1);
		verify(recipeRepository, times(1)).findAll();
	}
}
