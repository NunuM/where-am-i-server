package me.nunum.whereami;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.framework.interceptor.RequestTrackingFilter;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenSubResourceException;
import me.nunum.whereami.service.TaskManager;
import me.nunum.whereami.utils.AppConfig;
import org.glassfish.grizzly.http.server.DefaultErrorPageGenerator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class.
 */
public final class Main {
    // Base URI the Grizzly HTTP server will listen on
    private static final String BASE_URI = String.format("http://0.0.0.0:%s/%s", AppConfig.APP_PORT, AppConfig.APP_API_PATH);

    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    private static HttpServer startServer() {

        // create a resource config that scans for JAX-RS resources and providers
        // in me.nunum.whereami.facade package
        final ResourceConfig rc = new ResourceConfig().packages("me.nunum.whereami.facade");
        rc.setApplicationName(AppConfig.APP_NAME);

        rc.register(PrincipalInterceptor.class);
        rc.register(RequestTrackingFilter.class);

        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        rc.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY, LoggingFeature.Verbosity.PAYLOAD_ANY);
        rc.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, Level.INFO.getName());

        rc.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        rc.property(ServerProperties.MONITORING_STATISTICS_ENABLED, true);

        rc.register(RolesAllowedDynamicFeature.class);

        rc.register(EntityNotFoundException.class);
        rc.register(ForbiddenSubResourceException.class);


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
    public static void main(String[] args) throws IOException, InterruptedException {

        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();

        final HttpServer server = startServer();

        server.getServerConfiguration().setDefaultErrorPageGenerator(new DefaultErrorPageGenerator());

        StaticHttpHandler docsHandler = new StaticHttpHandler(System.getProperty("app.website.dir", ""));
        docsHandler.setFileCacheEnabled(false);

        ServerConfiguration cfg = server.getServerConfiguration();
        cfg.addHttpHandler(docsHandler, "/");

        LOGGER.log(Level.INFO, "Jersey app started with WADL available at http://{0}", AppConfig.APP_AUTHORITY);

        final Thread taskManager = new Thread(() -> TaskManager.getInstance().run(), "TaskManager");
        taskManager.start();

        LOGGER.fine("System Properties");
        System.getProperties().forEach((k, v) -> {
            LOGGER.fine(k + ":" + v);
        });

        taskManager.join();

        server.shutdown();

    }
}

