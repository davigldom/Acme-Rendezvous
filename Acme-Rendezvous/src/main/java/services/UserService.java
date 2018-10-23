
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.UserRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Answer;
import domain.Rendezvous;
import domain.User;
import forms.RegisterUser;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository		userRepository;

	@Autowired
	private UserAccountService	userAccountService;

	@Autowired
	private ActorService		actorService;


	public User create() {

		final UserAccount userAccount = this.userAccountService.create();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.USER);
		userAccount.addAuthority(authority);

		final User result;
		final Collection<Rendezvous> rendezvouses = new HashSet<Rendezvous>();
		final Collection<Answer> answers = new HashSet<Answer>();
		result = new User();
		result.setUserAccount(userAccount);
		result.setRendezvouses(rendezvouses);
		result.setAnswers(answers);
		//		result.setRequests(new ArrayList<Request>());

		return result;
	}

	public User findOne(final int userId) {
		User result;
		Assert.isTrue(userId != 0);
		result = this.userRepository.findOne(userId);
		Assert.notNull(result);
		return result;
	}
	
	public User findOneToEdit(final int userId) {
		User result;
		Assert.isTrue(userId != 0);
		result = this.userRepository.findOne(userId);
		Assert.notNull(result);
		Assert.isTrue(result.equals(this.findByPrincipal()));
		return result;
	}

	public Collection<User> findAll() {
		Collection<User> result;

		result = this.userRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public User save(final User user) {
		User result;
		Assert.notNull(user);
		Assert.notNull(user.getUserAccount().getUsername());
		Assert.notNull(user.getUserAccount().getPassword());
		if (user.getId() == 0) {
			String passwordHashed = null;
			final Md5PasswordEncoder encoder = new Md5PasswordEncoder();
			passwordHashed = encoder.encodePassword(user.getUserAccount().getPassword(), null);
			user.getUserAccount().setPassword(passwordHashed);
		}
		result = this.userRepository.save(user);
		return result;
	}

	public Collection<User> findAttendants(final int rendezvousId) {
		Collection<User> result = null;

		result = this.userRepository.findAttendants(rendezvousId);

		Assert.notNull(result);

		return result;
	}

	public User findByPrincipal() {
		User result;
		final UserAccount userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		result = this.findByUserAccount(userAccount);
		Assert.notNull(result);

		return result;
	}

	public User findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		User result;
		result = this.userRepository.findByUserAccountId(userAccount.getId());
		Assert.notNull(result);

		return result;
	}

	public void delete(final User user) {
		Assert.isTrue(user.getId() != 0);
		Assert.isTrue(this.actorService.findByPrincipal().equals(user));
		this.userRepository.delete(user);
		Assert.isTrue(!this.userRepository.findAll().contains(user));
	}

	public boolean isAuthenticated() {
		boolean result = true;
		final Authentication authentication;

		authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS")))
			result = false;

		return result;
	}

	public User findRespondent(final int answerId) {
		User result;

		result = this.userRepository.findRespondent(answerId);
		Assert.notNull(result);

		return result;
	}
	
	public void flush(){
		this.userRepository.flush();
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public User reconstruct(final User user, final BindingResult binding) {
		User userStored;

		if (user.getId() != 0) {
			userStored = this.userRepository.findOne(user.getId());
			//			result.setEmail(user.getEmail());
			//			result.setName(user.getName());
			//			result.setPhone(user.getPhone());
			//			result.setSurname(user.getSurname());
			//			result.setPostalAddress(user.getPostalAddress());

			user.setAnswers(userStored.getAnswers());
			user.setId(userStored.getId());
			user.setRendezvouses(userStored.getRendezvouses());
			user.setUserAccount(userStored.getUserAccount());
			user.setVersion(userStored.getVersion());
		}
		this.validator.validate(user, binding);
		return user;

	}

	public User reconstructRegister(final RegisterUser user, final BindingResult binding) {
		User result;
		Assert.isTrue(user.isAcceptedTerms());
		Assert.isTrue(user.getPassword().equals(user.getRepeatedPassword()));
		result = this.create();

		result.setEmail(user.getEmail());
		result.setName(user.getName());
		result.setPhone(user.getPhone());
		result.setPostalAddress(user.getPostalAddress());
		result.setSurname(user.getSurname());
		result.setBirthdate(user.getBirthdate());

		result.getUserAccount().setUsername(user.getUsername());
		result.getUserAccount().setPassword(user.getPassword());

		this.validator.validate(result, binding);

		return result;
	}

}
