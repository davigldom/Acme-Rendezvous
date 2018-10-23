
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CategoryRepository;
import domain.AcmeService;
import domain.Administrator;
import domain.Category;

@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository		categoryRepository;

	@Autowired
	private AdministratorService	adminService;


	public Category create() {
		final Category result;
		result = new Category();
		return result;
	}

	public Category findOne(final int categoryId) {
		Category result;
		Assert.isTrue(categoryId != 0);
		result = this.categoryRepository.findOne(categoryId);
		Assert.notNull(result);
		return result;
	}

	public Category findOneToEdit(final int categoryId) {
		Category result;

		Assert.isTrue(categoryId != 0);
		result = this.categoryRepository.findOne(categoryId);

		final Administrator principal = this.adminService.findByPrincipal();
		Assert.notNull(principal);

		Assert.isTrue(!result.getName().equals("Default"));
		Assert.notNull(result);
		return result;
	}

	public Collection<Category> findAll() {
		Collection<Category> result;
		result = this.categoryRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Collection<Category> findChildren(final int categoryId) {
		Collection<Category> result;
		result = this.categoryRepository.findChildren(categoryId);
		Assert.notNull(result);
		return result;
	}

	public Category save(final Category category) {
		Category result;
		Assert.notNull(category);
		final Administrator principal = this.adminService.findByPrincipal();
		Assert.notNull(principal);

		Assert.isTrue(!category.getName().equals("Default"));
		result = this.categoryRepository.save(category);

		return result;
	}

	public void delete(final Category category) {
		Assert.notNull(category);

		final Administrator principal = this.adminService.findByPrincipal();
		Assert.notNull(principal);

		Assert.isTrue(!category.getName().equals("Default"));
		this.categoryRepository.delete(category);

	}

	public Collection<Category> findAllChildren() {
		Collection<Category> result;
		result = this.categoryRepository.findAllChildren();
		Assert.notNull(result);
		return result;
	}

	public Collection<Category> getRoots() {
		Collection<Category> result;
		result = this.categoryRepository.getRoots();
		Assert.notNull(result);
		return result;
	}

	public Category findDefaultCategory() {
		Category result;
		result = this.categoryRepository.findDefaultCategory();
		Assert.notNull(result);
		return result;
	}

	// Dashboard
	public double averageNumberCategoriesPerRendezvous() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.categoryRepository.averageNumberCategoriesPerRendezvous();
	}

	public void flush() {
		this.categoryRepository.flush();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public Category reconstruct(final Category category, final BindingResult binding) {
		Category result;

		if (category.getId() == 0)
			category.setServices(new ArrayList<AcmeService>());
		else {
			result = this.categoryRepository.findOne(category.getId());
			// result.setPicture(category.getPicture());
			// result.setText(category.getText());
			category.setId(result.getId());
			category.setServices(result.getServices());
			category.setVersion(result.getVersion());

		}
		this.validator.validate(category, binding);
		return category;

	}

}
