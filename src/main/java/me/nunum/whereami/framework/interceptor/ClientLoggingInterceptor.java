package me.nunum.whereami.framework.interceptor;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;
import java.util.logging.Logger;

public class ClientLoggingInterceptor implements ClientResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(ClientLoggingInterceptor.class.getSimpleName());

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
            throws IOException {

        final StringBuffer debugBuffer = new StringBuffer();

        debugBuffer.append("Client request\n");
        debugBuffer.append(String.format("%s: %s%n", requestContext.getMethod(), requestContext.getUri().toString()));

        requestContext.getHeaders()
                .forEach((k, v) -> debugBuffer.append(String.format("%s: %s%n", k, v)));

        debugBuffer.append("\n");

        debugBuffer.append(String.format("%d %n", responseContext.getStatus()));
        responseContext.getHeaders()
                .forEach((k, v) -> debugBuffer.append(String.format("%s: %s%n", k, v)));

        LOGGER.info(debugBuffer.toString());
    }
}
