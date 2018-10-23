
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AcmeServiceRepository;
import security.LoginService;
import domain.AcmeService;
import domain.Administrator;
import domain.Manager;
import domain.Request;

@Service
@Transactional
public class AcmeServiceService {

	@Autowired
	private AcmeServiceRepository	acmeServiceRepository;

	@Autowired
	private ManagerService			managerService;

	@Autowired
	private AdministratorService	adminService;


	public AcmeService create() {
		final AcmeService result;
		result = new AcmeService();
		return result;
	}

	public AcmeService findOne(final int serviceId) {
		Assert.notNull(LoginService.getPrincipal());
		AcmeService result;
		Assert.isTrue(serviceId != 0);
		result = this.acmeServiceRepository.findOne(serviceId);
		Assert.notNull(result);
		return result;
	}

	public Collection<AcmeService> findAll() {
		Assert.notNull(LoginService.getPrincipal());
		return this.acmeServiceRepository.findAll();
	}

	public AcmeService save(final AcmeService service) {
		AcmeService result;
		final Manager principal = this.managerService.findByPrincipal();
		Assert.notNull(service);

		if (service.getId() != 0)
			Assert.isTrue(principal.getServices().contains(service));

		// If a service is cancelled, updating it wouldn't be logical.
		Assert.isTrue(!service.isCancelled());

		result = this.acmeServiceRepository.save(service);

		if (service.getId() == 0)
			principal.getServices().add(result);

		this.managerService.save(principal);

		return result;
	}

	public void delete(final AcmeService service) {
		final Manager principal = this.managerService.findByPrincipal();
		Assert.notNull(service);
		Assert.isTrue(principal.getServices().contains(service));
		Assert.isTrue(!service.isCancelled());
		Assert.isTrue(service.getRequests().isEmpty());

		principal.getServices().remove(service);

		this.acmeServiceRepository.delete(service);

		this.managerService.save(principal);

	}

	public void cancel(final AcmeService service) {
		final Administrator principal = this.adminService.findByPrincipal();
		Assert.notNull(principal);
		Assert.notNull(service);
		Assert.isTrue(!service.isCancelled());
		final Boolean cancelled = true;
		service.setCancelled(cancelled);
	}

	public Collection<AcmeService> findByPrincipal() {
		Collection<AcmeService> result;
		final Manager principal = this.managerService.findByPrincipal();

		result = this.acmeServiceRepository.findByPrincipal(principal.getId());

		return result;
	}

	public AcmeService findOneToEdit(final int acmeServiceId) {
		AcmeService result;
		final Manager principal = this.managerService.findByPrincipal();

		Assert.isTrue(acmeServiceId != 0);
		result = this.acmeServiceRepository.findOne(acmeServiceId);
		Assert.notNull(result);
		Assert.isTrue(!result.isCancelled());
		Assert.isTrue(principal.getServices().contains(result));

		return result;
	}

	//Dashboard

	public double averageRatioServicesPerCategory() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.acmeServiceRepository.averageRatioServicesPerCategory();
	}

	public double averageServicesRequestedPerRendezvous() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.acmeServiceRepository.averageServicesRequestedPerRendezvous();
	}

	public int minServicesRequestedPerRendezvous() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.acmeServiceRepository.minServicesRequestedPerRendezvous();
	}

	public int maxServicesRequestedPerRendezvous() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.acmeServiceRepository.maxServicesRequestedPerRendezvous();
	}

	public double standardDeviationServicesRequestedPerRendezvous() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.acmeServiceRepository.standardDeviationServicesRequestedPerRendezvous();
	}

	public Page<AcmeService> getTop3Selling() {
		Administrator admin;

		admin = this.adminService.findByPrincipal();
		Assert.notNull(admin);

		return this.acmeServiceRepository.getTop3Selling(new PageRequest(0, 3));
	}

	public void flush() {
		this.acmeServiceRepository.flush();
	}


	// Reconstruct

	@Autowired
	private Validator	validator;


	public AcmeService reconstruct(final AcmeService service, final BindingResult binding) {
		AcmeService serviceStored;

		if (service.getId() == 0) {
			service.setRequests(new ArrayList<Request>());
			service.setCancelled(false);
		} else {
			serviceStored = this.acmeServiceRepository.findOne(service.getId());
			service.setId(serviceStored.getId());
			service.setRequests(serviceStored.getRequests());
			service.setVersion(serviceStored.getVersion());
			service.setCancelled(serviceStored.isCancelled());

		}
		this.validator.validate(service, binding);
		return service;

	}

}
