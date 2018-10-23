
package services;

import java.util.Calendar;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ActorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Actor;

@Service
@Transactional
public class ActorService {

	@Autowired
	private ActorRepository	actorRepository;


	public Actor findOne(final int actorId) {

		Actor result;
		result = this.actorRepository.findOne(actorId);
		Assert.notNull(result);
		return result;
	}

	public Actor findOneToEdit(final int actorId) {
		Actor result;
		result = this.findOne(actorId);
		Assert.isTrue(this.findByPrincipal().equals(result));

		return result;
	}

	public Actor findByPrincipal() {
		Actor result;
		final UserAccount userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		result = this.findByUserAccount(userAccount);
		Assert.notNull(result);

		return result;
	}

	public Actor findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		Actor result;
		result = this.actorRepository.findByUserAccountId(userAccount.getId());
		Assert.notNull(result);

		return result;
	}

	public Actor findByUserAccountId(final int actorId) {
		Actor result;
		result = this.actorRepository.findByUserAccountId(actorId);
		return result;
	}

	public boolean isAdult() {
		boolean result = false;
		final Actor principal = this.findByPrincipal();
		final Calendar birthdate = Calendar.getInstance();
		birthdate.setTime(principal.getBirthdate());
		final Calendar currentTime = Calendar.getInstance();

		if ((currentTime.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR)) > 18)
			result = true;
		else if ((currentTime.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR)) == 18)
			if ((currentTime.get(Calendar.MONTH) - birthdate.get(Calendar.MONTH)) > 0)
				result = true;
			else if ((currentTime.get(Calendar.MONTH) - birthdate.get(Calendar.MONTH)) == 0)
				if ((currentTime.get(Calendar.DAY_OF_MONTH) - birthdate.get(Calendar.DAY_OF_MONTH)) >= 0)
					result = true;

		if (principal.getUserAccount().getAuthorities().contains(Authority.ADMIN))
			result = true;
		return result;
	}
}
