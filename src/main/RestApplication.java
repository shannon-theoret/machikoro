package main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Response;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/machikoroapp")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add( Hello.class );
        set.add( GameService.class );
        set.add(CorsFilter.class);
        return set;
    }



}

