package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.model.Role;
import me.nunum.whereami.model.persistance.jpa.RoleRepositoryJpa;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.ProviderRepository;
import me.nunum.whereami.model.persistance.RoleRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.ProviderRepositoryJpa;
import me.nunum.whereami.model.request.NewProviderRequest;
import me.nunum.whereami.service.NotifyService;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Optional;

public class ProviderController implements AutoCloseable {

    private final RoleRepository roleRepository;
    private final DeviceRepository deviceRepository;
    private final ProviderRepository providerRepository;

    public ProviderController() {
        this.roleRepository = new RoleRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.providerRepository = new ProviderRepositoryJpa();
    }

    public DTO registerNewProviderRequest(Principal principal, NewProviderRequest request) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        final Provider provider = request.build(device);

        NotifyService.newProviderRequest(provider).run();

        return this.providerRepository.save(provider).toDTO();
    }

    @Override
    public void close() throws Exception {
        this.deviceRepository.close();
    }


    public DTO confirmNewProviderRequest(Principal userPrincipal, String token) {

        Optional<Provider> optionalProvider = this.providerRepository.findByToken(token);

        if (!optionalProvider.isPresent()) {
            throw new EntityNotFoundException("");
        }

        final Provider provider = optionalProvider.get();

        provider.providerHasConfirmedEmail();

        final Role role = this.roleRepository.findRole(Role.PROVIDER);

        role.addDevice(provider.getRequester());

        this.roleRepository.save(role);

        return this.providerRepository.save(provider).toDTO();
    }
}
