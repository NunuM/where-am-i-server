package me.nunum.whereami.framework.interceptor;

import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.ws.rs.container.*;
import java.io.IOException;
import java.util.UUID;

@PreMatching
@Priority(Integer.MIN_VALUE)
public class RequestTrackingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    static final String REQUEST_ID_HEADER = "x-request-id";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String requestId = requestContext.getHeaderString(REQUEST_ID_HEADER);

        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
            requestContext.getHeaders().putSingle(REQUEST_ID_HEADER, requestId);
        }

        MDC.put(REQUEST_ID_HEADER, requestId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle(REQUEST_ID_HEADER, requestContext.getHeaderString(REQUEST_ID_HEADER));
        MDC.clear();
    }
}
