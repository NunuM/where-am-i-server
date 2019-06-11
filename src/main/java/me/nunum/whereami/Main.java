package me.nunum.whereami;

import me.nunum.whereami.facade.ApiListingResource;
import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenSubResourceException;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
import me.nunum.whereami.service.TaskManager;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class.
 */
public final class Main {
    // Base URI the Grizzly HTTP server will listen on
    private static final String BASE_URI = String.format("http://0.0.0.0:%s", System.getProperty("app.server.port", "8080"));

    private static final Logger LOGGER = Logger.getLogger("Main");

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    private static HttpServer startServer() {

        // create a resource config that scans for JAX-RS resources and providers
        // in me.nunum.whereami.facade package
        final ResourceConfig rc = new ResourceConfig().packages("me.nunum.whereami.facade");
        rc.setApplicationName("WhereAmI");

        rc.register(PrincipalInterceptor.class);
        rc.register(ApiListingResource.class);
        rc.register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        rc.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY, LoggingFeature.Verbosity.PAYLOAD_ANY);
        rc.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, Level.INFO.getName());

        rc.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

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


//        PostRepository repository = new PostRepositoryJpa();
//
//        for (int i = 0; i < 10; i++) {
//            repository.save(new Post("Test title" + i, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/R_logo.svg/120px-R_logo.svg.png", "https://nunum.me"));
//        }
//
//        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
//        final Device device = deviceRepository.save(new Device("das"));
//
//        RoleRepository roleRepository = new RoleRepositoryJpa();
//        roleRepository.save(new Role("admin"));
//        final Role role = new Role("provider");
//        role.addDevice(device);
//        roleRepository.save(role);
//
//        AlgorithmRepository algorithmRepository = new AlgorithmRepositoryJpa();
//        Algorithm algorithm = algorithmRepository.save(new Algorithm("Mean", "Nuno", "example.pt", true, device));
//
//        ProviderRepository providerRepository = new ProviderRepositoryJpa();
//        final Provider provider = providerRepository.save(new Provider("nuno@nunum.me", UUID.randomUUID().toString(), true, device));
//
//        final HashMap<String, String> map = new HashMap<>(3);
//        map.put(AlgorithmProvider.HTTP_PROVIDER_INGESTION_URL_KEY, "http://www.mocky.io/v2/5cfd86b93200007100ccd52f");
//        map.put(AlgorithmProvider.HTTP_PROVIDER_PREDICTION_URL_KEY, "http://www.mocky.io/v2/5cfd86b93200007100ccd52f");
//        algorithm.addProvider(new AlgorithmProvider(provider, AlgorithmProvider.METHOD.HTTP, map));
//        algorithm = algorithmRepository.save(algorithm);
//
//
//        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();
//        final Localization localization = localizationRepository.save(new Localization("Q", "Q", device));
//
//        PositionRepository positionRepository = new PostitionRepositoryJpa();
//        positionRepository.save(new Position("localization", localization));


        final HttpServer server = startServer();

        ClassLoader loader = Main.class.getClassLoader();
        CLStaticHttpHandler docsHandler = new CLStaticHttpHandler(loader, "swagger-ui/dist/");
        docsHandler.setFileCacheEnabled(false);

        ServerConfiguration cfg = server.getServerConfiguration();
        cfg.addHttpHandler(docsHandler, "/docs/");

        LOGGER.log(Level.INFO, "Jersey app started with WADL available at "
                + "{0}", BASE_URI);

        final Thread taskManager = new Thread(() -> TaskManager.getInstance().run(), "TaskManager");
        taskManager.start();

        taskManager.join();

        server.shutdown();

    }
}

