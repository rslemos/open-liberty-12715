package my;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@ApplicationPath("/myapp")
public class MyApplication extends Application {
  @Context
  private SecurityContext securityContext;

  @Override
  public Map<String, Object> getProperties() {
    return Collections.singletonMap("securityContext", securityContext);
  }
}
