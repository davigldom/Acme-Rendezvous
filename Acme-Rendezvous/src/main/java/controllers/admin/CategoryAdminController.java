package controllers.admin;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CategoryService;
import controllers.AbstractController;
import domain.AcmeService;
import domain.Category;

@Controller
@RequestMapping("/category/admin")
public class CategoryAdminController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private CategoryService categoryService;

	// Constructors -----------------------------------------------------------

	public CategoryAdminController() {
		super();
	}

	// Listing ----------------------------------------------------------------
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Category category;
		category = this.categoryService.create();
		result = this.createEditModelAndView(category);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int categoryId) {
		ModelAndView result;
		Category category;

		category = this.categoryService.findOneToEdit(categoryId);
		Assert.notNull(category);

		result = this.createEditModelAndView(category);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Category category, final BindingResult binding) {
		ModelAndView result;

		category = this.categoryService.reconstruct(category, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(category);
		else
			try {
				final Category categorySaved = this.categoryService
						.save(category);
				result = new ModelAndView(
						"redirect:/category/list.do?categoryId="
								+ categorySaved.getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(category,
						"category.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Category category, final BindingResult binding) {
		ModelAndView result;
		category = this.categoryService.reconstruct(category, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(category);
		else
			try {

				//If a user is trying to delete the default category,
				//we must ignore these actions (and the service will take care of it)
				if (!category.getName().equals("Default")) {
					for (Category child : this.categoryService
							.findChildren(category.getId())) {
						child.setParent(null);
					}

					// When a category is deleted, all its services now belong
					// to an abstract category, named "Default"
					for (AcmeService service : category.getServices()) {
						service.setCategory(this.categoryService
								.findDefaultCategory());
					}
				}
				this.categoryService.delete(category);
				result = new ModelAndView("redirect:/category/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(category,
						"category.commit.error");
			}

		return result;
	}

	protected ModelAndView createEditModelAndView(final Category category) {
		ModelAndView result;

		result = this.createEditModelAndView(category, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Category category,
			final String message) {
		ModelAndView result = null;

		result = new ModelAndView("category/admin/edit");

		Collection<Category> categories = this.categoryService.findAll();
		if (category.getId() != 0)
			categories.remove(category);

		result.addObject("category", category);
		result.addObject("categories", categories);
		result.addObject("message", message);

		return result;
	}

}
