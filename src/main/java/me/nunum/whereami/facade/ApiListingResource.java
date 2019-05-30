package me.nunum.whereami.facade;

import io.swagger.annotations.ApiOperation;
import io.swagger.config.FilterFactory;
import io.swagger.config.Scanner;
import io.swagger.config.SwaggerConfig;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.JaxrsScanner;
import io.swagger.jaxrs.config.ReaderConfig;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Info;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.util.Yaml;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/api/doc")
@Singleton
@PermitAll
public class ApiListingResource {
    static boolean initialized = false;

    private static final Logger LOGGER = Logger.getLogger(ApiListingResource.class.getSimpleName());


    private Swagger mSwaggerConfig;

    public ApiListingResource() {
        mSwaggerConfig = new Swagger();
        mSwaggerConfig.setBasePath("/");
        mSwaggerConfig.info(new Info().title("WhereAmI"));
        mSwaggerConfig.setHost("localhost:8080");
        mSwaggerConfig.setSchemes(Arrays.asList(Scheme.HTTP));
    }

    public ApiListingResource(Swagger swagger) {
        this.mSwaggerConfig = swagger;
    }

    protected synchronized Swagger scan(Application app) {
        Swagger swagger = null;
        Scanner scanner = new Scanner() {
            @Override
            public Set<Class<?>> classes() {
                return app.getClasses();
            }

            @Override
            public boolean getPrettyPrint() {
                return false;
            }

            @Override
            public void setPrettyPrint(boolean b) {

            }
        };
        LOGGER.log(Level.FINE, "using scanner " + scanner);
        SwaggerSerializers.setPrettyPrint(scanner.getPrettyPrint());
        swagger = this.mSwaggerConfig;
        new HashSet();
        Set classes;

        if (scanner instanceof JaxrsScanner) {
            classes = null;
        } else {
            classes = scanner.classes();
        }

        if (classes != null) {
            Reader reader = new Reader(swagger, new ReaderConfig() {
                @Override
                public boolean isScanAllResources() {
                    return false;
                }

                @Override
                public Collection<String> getIgnoredRoutes() {
                    return new ArrayList<>();
                }
            });
            swagger = reader.read(classes);
            if (scanner instanceof SwaggerConfig) {
                swagger = ((SwaggerConfig) scanner).configure(swagger);
            } else {
                SwaggerConfig configurator = new SwaggerConfig() {
                    @Override
                    public Swagger configure(Swagger swagger) {
                        return swagger;
                    }

                    @Override
                    public String getFilterClass() {
                        return "";
                    }
                };
                LOGGER.log(Level.FINE, "configuring swagger with " + configurator);
                configurator.configure(swagger);
            }

        }

        initialized = true;
        return swagger;
    }

    @GET
    @Produces({"application/json"})
    @Path("/swagger.json")
    @ApiOperation(
            value = "The swagger definition in JSON",
            hidden = true
    )
    public Response getListingJson(@Context Application app, @Context HttpHeaders headers, @Context UriInfo uriInfo) {
        Swagger swagger = this.mSwaggerConfig;
        if (!initialized) {
            this.mSwaggerConfig = this.scan(app);
        }

        if (swagger != null) {
            SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
            if (filterImpl != null) {
                SpecFilter f = new SpecFilter();
                swagger = f.filter(swagger, filterImpl, this.getQueryParams(uriInfo.getQueryParameters()), this.getCookies(headers), this.getHeaders(headers));
            }

            return Response.ok().entity(swagger).build();
        } else {
            return Response.status(404).build();
        }
    }

    @GET
    @Produces({"application/yaml"})
    @Path("/swagger.yaml")
    @ApiOperation(
            value = "The swagger definition in YAML",
            hidden = true
    )
    public Response getListingYaml(@Context Application app, @Context HttpHeaders headers, @Context UriInfo uriInfo) {
        Swagger swagger = this.mSwaggerConfig;
        if (!initialized) {
            this.mSwaggerConfig = this.scan(app);
        }

        try {
            if (swagger != null) {
                SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
                this.LOGGER.log(Level.FINE, "using filter " + filterImpl);
                if (filterImpl != null) {
                    SpecFilter f = new SpecFilter();
                    swagger = f.filter(swagger, filterImpl, this.getQueryParams(uriInfo.getQueryParameters()), this.getCookies(headers), this.getHeaders(headers));
                }

                String yaml = Yaml.mapper().writeValueAsString(swagger);
                String[] parts = yaml.split("\n");
                StringBuilder b = new StringBuilder();
                String[] arr$ = parts;
                int len$ = parts.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    String part = arr$[i$];
                    int pos = part.indexOf("!<");
                    int endPos = part.indexOf(">");
                    b.append(part);
                    b.append("\n");
                }

                return Response.ok().entity(b.toString()).type("application/yaml").build();
            }
        } catch (Exception var16) {
            LOGGER.log(Level.SEVERE, "Error listing swagger", var16);
        }

        return Response.status(404).build();
    }

    protected Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
        Map<String, List<String>> output = new HashMap();
        if (params != null) {
            Iterator i$ = params.keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                List<String> values = (List) params.get(key);
                output.put(key, values);
            }
        }

        return output;
    }

    protected Map<String, String> getCookies(HttpHeaders headers) {
        Map<String, String> output = new HashMap();
        if (headers != null) {
            Iterator i$ = headers.getCookies().keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                Cookie cookie = (Cookie) headers.getCookies().get(key);
                output.put(key, cookie.getValue());
            }
        }

        return output;
    }

    protected Map<String, List<String>> getHeaders(HttpHeaders headers) {
        Map<String, List<String>> output = new HashMap();
        if (headers != null) {
            Iterator i$ = headers.getRequestHeaders().keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                List<String> values = (List) headers.getRequestHeaders().get(key);
                output.put(key, values);
            }
        }

        return output;
    }
}
