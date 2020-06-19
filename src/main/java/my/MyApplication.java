package my;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@ApplicationPath("/myapp")
public class MyApplication extends Application {
  private SecurityContext securityContext1;
  @Context
  private SecurityContext securityContext2;
  private SecurityContext securityContext3 = null;

  // Resource classes with singleton scope can only have ServletConfig or ServletContext instances injected through their constructors
  // public MyApplication(@Context SecurityContext securityContext) {
  //   securityContext3 = securityContext;
  // }

  @Context
  public void setSecurityContext(SecurityContext securityContext) {
    securityContext1 = securityContext;
  }

  @Override
  public Map<String, Object> getProperties() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("securityContext1", securityContext1);
    properties.put("securityContext2", securityContext2);
    // properties.put("securityContext3", securityContext3);
    return properties;
  }
}
