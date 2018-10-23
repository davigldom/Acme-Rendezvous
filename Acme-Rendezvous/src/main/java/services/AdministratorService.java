
package services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AdministratorRepository;
import security.LoginService;
import security.UserAccount;
import domain.Administrator;

@Service
@Transactional
public class AdministratorService {

	@Autowired
	private AdministratorRepository	administratorRepository;


	public Administrator findByPrincipal() {
		Administrator result;
		final UserAccount administratorAccount = LoginService.getPrincipal();
		Assert.notNull(administratorAccount);
		result = this.findByUserAccount(administratorAccount);
		Assert.notNull(result);

		return result;
	}

	public Administrator findByUserAccount(final UserAccount administratorAccount) {
		Assert.notNull(administratorAccount);
		Administrator result;
		result = this.administratorRepository.findByUserAccountId(administratorAccount.getId());
		Assert.notNull(result);

		return result;
	}

	public Administrator save(final Administrator administrator) {
		Administrator result;
		Assert.notNull(administrator);
		Assert.isTrue(administrator.getId() != 0);
		Assert.isTrue(administrator.equals(this.findByPrincipal()));
		result = this.administratorRepository.save(administrator);
		return result;
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public Administrator reconstruct(final Administrator admin, final BindingResult binding) {
		Administrator adminStored;

		if (admin.getId() != 0) {
			adminStored = this.administratorRepository.findOne(admin.getId());
			//			result.setEmail(admin.getEmail());
			//			result.setName(admin.getName());
			//			result.setPhone(admin.getPhone());
			//			result.setSurname(admin.getSurname());
			//			result.setPostalAddress(admin.getPostalAddress());
			admin.setBirthdate(adminStored.getBirthdate());
			admin.setId(adminStored.getId());
			admin.setUserAccount(adminStored.getUserAccount());
			admin.setVersion(adminStored.getVersion());
		}
		this.validator.validate(admin, binding);
		return admin;

	}

}
