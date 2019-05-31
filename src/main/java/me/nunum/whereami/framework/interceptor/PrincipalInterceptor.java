package me.nunum.whereami.framework.interceptor;

import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.dto.ErrorDTO;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.utils.AppConfig;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Provider
@PreMatching
public class PrincipalInterceptor implements ContainerRequestFilter {


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (requestContext.getUriInfo().getPath().contains("swagger.json")) {
            return;
        }

        final Optional<String> instanceHeader = Optional.ofNullable(requestContext.getHeaderString(AppConfig.X_APP_HEADER));

        instanceHeader.ifPresent(instance -> requestContext.setSecurityContext(new DeviceSecurityContext(instance)));

        if (!instanceHeader.isPresent()) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDTO.fromXAppMissingHeader()).build());
        }
    }

    static final class DeviceSecurityContext
            implements SecurityContext,
            Principal {

        private final String instance;

        public DeviceSecurityContext(String instance) {
            this.instance = instance;
        }

        @Override
        public Principal getUserPrincipal() {
            return this;
        }

        @Override
        public boolean isUserInRole(String role) {

            try (final DeviceRepository deviceRepository = new DeviceRepositoryJpa()) {
                Device device = deviceRepository.findOrPersist(this);
                return device.isInRole(role);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public String getAuthenticationScheme() {
            return null;
        }

        @Override
        public String getName() {
            return instance;
        }
    }
}
