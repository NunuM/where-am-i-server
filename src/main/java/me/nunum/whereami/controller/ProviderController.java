package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.model.Role;
import me.nunum.whereami.model.exceptions.ForbiddenEntityModificationException;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.ProviderRepository;
import me.nunum.whereami.model.persistance.RoleRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.ProviderRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.RoleRepositoryJpa;
import me.nunum.whereami.model.request.NewProviderRequest;
import me.nunum.whereami.service.notification.NotifyService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Optional;

public class ProviderController implements AutoCloseable {

    private final RoleRepository roleRepository;
    private final DeviceRepository deviceRepository;
    private final ProviderRepository providerRepository;

    /**
     * Constructor
     */
    public ProviderController() {
        this.roleRepository = new RoleRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.providerRepository = new ProviderRepositoryJpa();
    }

    /**
     * Register device intention of having a Provider role
     *
     * @param principal See {@link Principal}
     * @param request   See {@link NewProviderRequest}
     * @return See {@link me.nunum.whereami.model.dto.ProviderDTO}
     */
    public DTO registerNewProviderRequest(Principal principal, NewProviderRequest request) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        if (device.isInRole(Role.PROVIDER)) {
            Optional<Provider> providerOptional = this.providerRepository.findByDevice(device);
            if (providerOptional.isPresent()) {
                return providerOptional.get().toDTO();
            } else {
                return this.providerRepository.save(request.buildConfirmed(device)).toDTO();
            }
        }

        final Provider provider = request.build(device);

        NotifyService.newProviderRequest(provider).run();

        return this.providerRepository.save(provider).toDTO();
    }


    /**
     * Confirmed the requester email address, then will be added
     * this device to a Provider role list.
     *
     * @param userPrincipal See {@link Principal}
     * @param token         Opaque string
     * @return See {@link me.nunum.whereami.model.dto.ProviderDTO}
     * @throws EntityNotFoundException              Token does not belongs to any provider
     * @throws ForbiddenEntityModificationException The device that request the validation
     *                                              must be the same that make the verification
     */
    public DTO confirmNewProviderRequest(Principal userPrincipal, String token) {

        final Device device = this.deviceRepository.findOrPersist(userPrincipal);

        Optional<Provider> optionalProvider = this.providerRepository.findByToken(token);

        if (!optionalProvider.isPresent()) {
            throw new EntityNotFoundException("We do not encounter a request with that token");
        }

        final Provider provider = optionalProvider.get();

        if (!provider.getRequester().equals(device)) {
            throw new ForbiddenEntityModificationException("The request must be from the original requester");
        }

        if (provider.isConfirmed()) {
            return provider.toDTO();
        }

        provider.providerHasConfirmedEmail();

        final Role role = this.roleRepository.findRole(Role.PROVIDER);

        role.addDevice(provider.getRequester());

        this.roleRepository.save(role);

        return this.providerRepository.save(provider).toDTO();
    }


    @Override
    public void close() throws Exception {
        this.deviceRepository.close();
    }

}
