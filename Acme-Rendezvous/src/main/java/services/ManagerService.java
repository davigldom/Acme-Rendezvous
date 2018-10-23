
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ManagerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.AcmeService;
import domain.Administrator;
import domain.Manager;
import forms.RegisterManager;

@Service
@Transactional
public class ManagerService {

	@Autowired
	private ManagerRepository		managerRepository;

	@Autowired
	private UserAccountService		managerAccountService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private AcmeServiceService		acmeServiceService;

	@Autowired
	private AdministratorService	administratorService;


	public Manager create() {

		final UserAccount managerAccount = this.managerAccountService.create();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.MANAGER);
		managerAccount.addAuthority(authority);

		final Manager result;
		final Collection<AcmeService> services = new HashSet<AcmeService>();
		result = new Manager();
		result.setUserAccount(managerAccount);
		result.setServices(services);

		return result;
	}

	public Manager findOne(final int managerId) {
		Manager result;
		Assert.isTrue(managerId != 0);
		result = this.managerRepository.findOne(managerId);
		Assert.notNull(result);
		return result;
	}
	
	public Manager findOneToEdit(final int managerId) {
		Manager result;
		Assert.isTrue(managerId != 0);
		result = this.managerRepository.findOne(managerId);
		Assert.notNull(result);
		Assert.isTrue(result.equals(this.findByPrincipal()));
		return result;
	}


	public Collection<Manager> findAll() {
		Collection<Manager> result;

		result = this.managerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public void flush() {
		this.managerRepository.flush();
	}

	public Manager save(final Manager manager) {
		Manager result;
		Assert.notNull(manager);
		Assert.notNull(manager.getUserAccount().getUsername());
		Assert.notNull(manager.getUserAccount().getPassword());
		if (manager.getId() == 0) {
			String passwordHashed = null;
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			passwordHashed = encoder.encodePassword(manager.getUserAccount().getPassword(), null);
			manager.getUserAccount().setPassword(passwordHashed);
		}
		result = this.managerRepository.save(manager);
		return result;
	}

	public Manager findByPrincipal() {
		Manager result;
		final UserAccount managerAccount = LoginService.getPrincipal();
		Assert.notNull(managerAccount);
		result = this.findByUserAccount(managerAccount);
		Assert.notNull(result);

		return result;
	}

	public Manager findByUserAccount(final UserAccount managerAccount) {
		Assert.notNull(managerAccount);
		Manager result;
		result = this.managerRepository.findByUserAccountId(managerAccount.getId());
		Assert.notNull(result);

		return result;
	}

	public void delete(final Manager manager) {
		Assert.isTrue(manager.getId() != 0);
		Assert.isTrue(this.actorService.findByPrincipal().equals(manager));
		this.managerRepository.delete(manager);
		Assert.isTrue(!this.managerRepository.findAll().contains(manager));
	}

	public Manager findCreator(final int serviceId) {
		Manager result;
		Assert.notNull(this.acmeServiceService.findOne(serviceId));

		result = this.managerRepository.findCreator(serviceId);
		Assert.notNull(result);

		return result;
	}

	//Dashboard R6.2
	public Collection<Manager> getManagersProvidingMoreServicesThanAverage() {
		Administrator admin;
		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		return this.managerRepository.getManagersProvidingMoreServicesThanAverage();
	}

	public Page<Manager> getManagersWithMoreServicesCancelled() {
		Administrator admin;
		admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		return this.managerRepository.getManagersWithMoreServicesCancelled(new PageRequest(0, 3));
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public Manager reconstruct(final Manager manager, final BindingResult binding) {
		Manager managerStored;

		if (manager.getId() != 0) {
			managerStored = this.managerRepository.findOne(manager.getId());
			//			result.setEmail(manager.getEmail());
			//			result.setName(manager.getName());
			//			result.setPhone(manager.getPhone());
			//			result.setSurname(manager.getSurname());
			//			result.setPostalAddress(manager.getPostalAddress());

			manager.setServices(managerStored.getServices());
			manager.setId(managerStored.getId());
			manager.setUserAccount(managerStored.getUserAccount());
			manager.setVersion(managerStored.getVersion());
			//manager.setVat(managerStored.getVat());
		}
		this.validator.validate(manager, binding);
		return manager;

	}

	public Manager reconstructRegister(final RegisterManager manager, final BindingResult binding) {
		Manager result;
		Assert.isTrue(manager.isAcceptedTerms());
		Assert.isTrue(manager.getPassword().equals(manager.getRepeatedPassword()));
		result = this.create();

		result.setVat(manager.getVat());

		result.setEmail(manager.getEmail());
		result.setName(manager.getName());
		result.setPhone(manager.getPhone());
		result.setPostalAddress(manager.getPostalAddress());
		result.setSurname(manager.getSurname());
		result.setBirthdate(manager.getBirthdate());

		result.getUserAccount().setUsername(manager.getUsername());
		result.getUserAccount().setPassword(manager.getPassword());

		this.validator.validate(result, binding);

		return result;
	}

}
