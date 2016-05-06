package org.softclicker.server.http;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleContainerFactory;
import org.glassfish.jersey.simple.SimpleServer;
import org.softclicker.server.http.api.SoftClicker;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class ServerFactory {
    public static void createServer() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ResourceConfig(SoftClicker.class);
        SimpleServer server = SimpleContainerFactory.create(baseUri, config);
    }
}
