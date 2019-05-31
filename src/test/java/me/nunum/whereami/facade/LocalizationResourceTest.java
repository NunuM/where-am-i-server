package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static org.junit.Assert.*;

public class LocalizationResourceTest extends JerseyTest {


    @Override
    protected Application configure() {
        return new ResourceConfig(LocalizationResource.class).register(PrincipalInterceptor.class);
    }


    @Test
    public void retrieveLocalizations() {

    }

    @Test
    public void newLocalization() {
    }

    @Test
    public void deleteLocalization() {
    }

}