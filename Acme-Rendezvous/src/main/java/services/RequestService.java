
package services;

import java.util.Calendar;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.RequestRepository;
import domain.AcmeService;
import domain.Rendezvous;
import domain.Request;

@Service
@Transactional
public class RequestService {

	@Autowired
	private RequestRepository	requestRepository;

	@Autowired
	private RendezvousService	rendezvousService;


	public Request create() {
		final Request result;
		result = new Request();
		return result;
	}

	public Request findOne(final int requestId) {
		Request result;
		Assert.isTrue(requestId != 0);
		result = this.requestRepository.findOne(requestId);
		Assert.notNull(result);
		return result;
	}

	public Request save(final Request request, final AcmeService service, final Rendezvous rendezvous) {
		Request result;
		final Collection<Rendezvous> rendezvouses = this.rendezvousService.findWithoutService(service.getId());

		//		final User principal = this.userService.findByPrincipal();
		Assert.notNull(request);
		final Calendar currentTime = Calendar.getInstance();
		Assert.isTrue(request.getExpirationMonth() > currentTime.get(Calendar.MONTH));
		Assert.isTrue(request.getExpirationYear() > currentTime.get(Calendar.YEAR));
		Assert.isTrue(!service.isCancelled());
		Assert.isTrue(rendezvouses.contains(rendezvous));

		result = this.requestRepository.save(request);

		rendezvous.getRequests().add(result);
		service.getRequests().add(result);
		//		principal.getRequests().add(result);

		return result;
	}


	//Reconstruct

	@Autowired
	private Validator	validator;


	public Request reconstruct(final Request request, final AcmeService service, final BindingResult binding) {

		if (request.getId() == 0)
			request.setService(service);

		this.validator.validate(request, binding);
		return request;

	}

}
