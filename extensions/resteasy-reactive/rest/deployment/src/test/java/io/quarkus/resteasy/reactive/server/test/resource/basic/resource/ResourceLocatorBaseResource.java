package io.quarkus.resteasy.reactive.server.test.resource.basic.resource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class ResourceLocatorBaseResource {

    private static final Logger LOG = Logger.getLogger(ResourceLocatorBaseResource.class);

    @GET
    @Produces("*/*")
    public String getDefault(@Context UriInfo uri) {
        LOG.debug("Here in BaseResource");
        List<String> matchedURIs = uri.getMatchedURIs();
        return matchedURIs.toString();
    }

    @Path("base/{param}/resources")
    public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri) {
        LOG.debug("Here in BaseResource");
        Assertions.assertEquals("1", param);
        List<String> matchedURIs = uri.getMatchedURIs();
        Assertions.assertEquals(2, matchedURIs.size());
        Assertions.assertEquals("app/base/1/resources", matchedURIs.get(0));
        Assertions.assertEquals("", matchedURIs.get(1));
        for (String ancestor : matchedURIs)
            LOG.debug("   " + ancestor);

        LOG.debug("Uri Ancestors Object for Subresource.doGet():");
        Assertions.assertEquals(1, uri.getMatchedResources().size());
        Assertions.assertEquals(ResourceLocatorBaseResource.class, uri.getMatchedResources().get(0).getClass());
        return new ResourceLocatorSubresource();
    }

    @Path("proxy")
    public ResourceLocatorSubresource3Interface sub3() {
        return (ResourceLocatorSubresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[] { ResourceLocatorSubresource3Interface.class }, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(new ResourceLocatorSubresource3(), args);
                    }
                });
    }

    @Path("sub3/{param}/resources")
    public ResourceLocatorSubresource getSubresource() {
        return new ResourceLocatorSubresource();
    }

    @OPTIONS
    @Path("{any:.*}")
    public Object preflight() {
        return "Here might be a custom handler for HTTP OPTIONS method";
    }

}
