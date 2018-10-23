package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CategoryService;
import domain.Category;

@Controller
@RequestMapping("/category")
public class CategoryController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private CategoryService categoryService;

	// Constructors -----------------------------------------------------------

	public CategoryController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(
			@RequestParam(required = false) final Integer categoryId) {
		final ModelAndView result;
		Collection<Category> categories;
		Category category = null;

		if (categoryId != null){
			category = this.categoryService.findOne(categoryId);
			categories = this.categoryService.findChildren(categoryId);
		}else
			categories = this.categoryService.getRoots();

		result = new ModelAndView("category/list");

		result.addObject("categories", categories);
		result.addObject("category", category);
		result.addObject("requestURI", "category/list.do");

		return result;
	}


}
