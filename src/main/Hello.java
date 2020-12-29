package main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class Hello {
    @GET
    public Response getMessage() {
        HelloWorld output = new HelloWorld();
        output.setHello("Hi there");
        output.setWorld("World!!!");
        return Response.status(Response.Status.OK).entity(output).build();
    }

    public class HelloWorld {
        String hello;
        String world;

        public String getHello() {
            return hello;
        }

        public void setHello(String hello) {
            this.hello = hello;
        }

        public String getWorld() {
            return world;
        }

        public void setWorld(String world) {
            this.world = world;
        }
    }
}
