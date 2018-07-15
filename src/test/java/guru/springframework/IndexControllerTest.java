package guru.springframework;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import guru.springframework.controllers.IndexController;
import guru.springframework.services.RecipeService;

public class IndexControllerTest {
	private IndexController indexController;

	@Mock
	private RecipeService recipeServiceMock;

	@Mock
	private Model modelMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		indexController = new IndexController(recipeServiceMock);
	}

	@Test
	public void test_get_index_page() {
		String view = indexController.getIndexPage(modelMock);
		assertEquals("index", view);
		verify(recipeServiceMock, times(1)).getRecipes();
		verify(modelMock, times(1)).addAttribute(eq("recipes"), anySet());
	}
}
