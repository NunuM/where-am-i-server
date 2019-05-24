package me.nunum.whereami;

import me.nunum.whereami.facade.ApiListingResource;
import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.service.SchedulerService;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.bridge.SLF4JBridgeHandler;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    private static final String BASE_URI = "http://0.0.0.0:8080";

    private static final Logger LOGGER = Logger.getLogger("Main");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

        // create a resource config that scans for JAX-RS resources and providers
        // in me.nunum.whereami.facade package
        final ResourceConfig rc = new ResourceConfig().packages("me.nunum.whereami.facade");
        rc.setApplicationName("where");

        rc.register(PrincipalInterceptor.class);
        rc.register(ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        rc.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY, LoggingFeature.Verbosity.PAYLOAD_ANY);
        rc.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, Level.INFO.getName());

        rc.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();

        final HttpServer server = startServer();


        ClassLoader loader = Main.class.getClassLoader();
        CLStaticHttpHandler docsHandler = new CLStaticHttpHandler(loader, "swagger-ui/dist/");
        docsHandler.setFileCacheEnabled(false);

        ServerConfiguration cfg = server.getServerConfiguration();
        cfg.addHttpHandler(docsHandler, "/docs/");

        Main.LOGGER.log(Level.INFO,"Jersey app started with WADL available at "
                + "{0} \nHit enter to stop it...", BASE_URI);
        System.in.read();
        server.shutdown();

    }
}

