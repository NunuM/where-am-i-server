package me.nunum.whereami.framework.interceptor;

import me.nunum.whereami.utils.AppConfig;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

public class PrincipalInterceptor implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        final Optional<String> instanceHeader = Optional.ofNullable(requestContext.getHeaderString(AppConfig.X_APP_HEADER));

        instanceHeader.ifPresent(instance -> requestContext.setSecurityContext(new DeviceSecurityContext(instance)));

        if (!instanceHeader.isPresent()) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
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
            return false;
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
