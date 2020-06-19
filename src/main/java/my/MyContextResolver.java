package my;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class MyContextResolver implements ContextResolver<Jsonb> {

  private Application application1;
  @Context
  private Application application2;
  private Application application3 = null;

  private SecurityContext securityContext1;
  @Context
  private SecurityContext securityContext2;
  private SecurityContext securityContext3 = null;

  private SecurityContext securityContext1FromApplication1;
  private SecurityContext securityContext2FromApplication1;
  private SecurityContext securityContext3FromApplication1 = null;

  private SecurityContext securityContext1FromApplication3;
  private SecurityContext securityContext2FromApplication3;
  private SecurityContext securityContext3FromApplication3 = null;

  public MyContextResolver(@Context Application application, @Context SecurityContext securityContext) {
    application3 = application;
    securityContext1FromApplication3 = (SecurityContext)application3.getProperties().get("securityContext1");
    securityContext2FromApplication3 = (SecurityContext)application3.getProperties().get("securityContext2");
    securityContext3FromApplication3 = (SecurityContext)application3.getProperties().get("securityContext3");
    securityContext3 = securityContext;
  }

  @Context
  public void setApplication(Application application) {
    application1 = application;
    securityContext1FromApplication1 = (SecurityContext)application1.getProperties().get("securityContext1");
    securityContext2FromApplication1 = (SecurityContext)application1.getProperties().get("securityContext2");
    securityContext3FromApplication1 = (SecurityContext)application1.getProperties().get("securityContext3");
  }

  @Context
  public void setSecurityContext(SecurityContext securityContext) {
    securityContext1 = securityContext;
  }

  @Override
  public Jsonb getContext(Class<?> type) {
    SecurityContext securityContext1FromApplication1 = (SecurityContext)application1.getProperties().get("securityContext1");
    SecurityContext securityContext2FromApplication1 = (SecurityContext)application1.getProperties().get("securityContext2");
    SecurityContext securityContext3FromApplication1 = (SecurityContext)application1.getProperties().get("securityContext3");
    SecurityContext securityContext1FromApplication2 = (SecurityContext)application2.getProperties().get("securityContext1");
    SecurityContext securityContext2FromApplication2 = (SecurityContext)application2.getProperties().get("securityContext2");
    SecurityContext securityContext3FromApplication2 = (SecurityContext)application2.getProperties().get("securityContext3");
    SecurityContext securityContext1FromApplication3 = (SecurityContext)application3.getProperties().get("securityContext1");
    SecurityContext securityContext2FromApplication3 = (SecurityContext)application3.getProperties().get("securityContext2");
    SecurityContext securityContext3FromApplication3 = (SecurityContext)application3.getProperties().get("securityContext3");

    JsonbConfig config = new JsonbConfig()
      .withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
        @Override
        public boolean isVisible(Field field) {
          return useSecurityContextToVetProperty();
        }

        @Override
        public boolean isVisible(Method method) {
          return useSecurityContextToVetProperty();
        }

        private boolean useSecurityContextToVetProperty() {
          System.out.println("=== REPORT ===");

          // known FAILURES (from bugreport's first alternative)
          // 3 securityContexts directly injected into MyContextResolver
          report("securityContext1", securityContext1);
          report("securityContext2", securityContext2);
          report("securityContext3", securityContext3);

          // known SUCCESSES (from bugreport's second alternative)
          // 2 securityContexts from 3 applications
          // (with late grabbing; grabs reference only on point of usage)
          report("application1.securityContext1", (SecurityContext)application1.getProperties().get("securityContext1"));
          report("application2.securityContext1", (SecurityContext)application2.getProperties().get("securityContext1"));
          report("application3.securityContext1", (SecurityContext)application3.getProperties().get("securityContext1"));
          report("application1.securityContext2", (SecurityContext)application1.getProperties().get("securityContext2"));
          report("application2.securityContext2", (SecurityContext)application2.getProperties().get("securityContext2"));
          report("application3.securityContext2", (SecurityContext)application3.getProperties().get("securityContext2"));

          // SUCCESSES
          // 2 securityContext from 3 applications
          // (with mid grabbing; grabs reference on getContext)
          report("securityContext1FromApplication1", securityContext1FromApplication1);
          report("securityContext1FromApplication2", securityContext1FromApplication2);
          report("securityContext1FromApplication3", securityContext1FromApplication3);
          report("securityContext2FromApplication1", securityContext2FromApplication1);
          report("securityContext2FromApplication2", securityContext2FromApplication2);
          report("securityContext2FromApplication3", securityContext2FromApplication3);

          // SUCCESSES
          // 2 securityContext from 2 applications
          // (with early grabbing; grabs reference right when application gets injected)
          report("class.securityContext1FromApplication1", MyContextResolver.this.securityContext1FromApplication1);
          report("class.securityContext1FromApplication3", MyContextResolver.this.securityContext1FromApplication3);
          report("class.securityContext2FromApplication1", MyContextResolver.this.securityContext2FromApplication1);
          report("class.securityContext2FromApplication3", MyContextResolver.this.securityContext2FromApplication3);

          // expected failures, because applications' securityContext3 never gets injected
          report("application1.securityContext3", (SecurityContext)application1.getProperties().get("securityContext3"));
          report("application2.securityContext3", (SecurityContext)application2.getProperties().get("securityContext3"));
          report("application3.securityContext3", (SecurityContext)application3.getProperties().get("securityContext3"));
          report("securityContext3FromApplication1", securityContext3FromApplication1);
          report("securityContext3FromApplication2", securityContext3FromApplication2);
          report("securityContext3FromApplication3", securityContext3FromApplication3);
          report("class.securityContext3FromApplication1", MyContextResolver.this.securityContext3FromApplication1);
          report("class.securityContext3FromApplication3", MyContextResolver.this.securityContext3FromApplication3);

          System.out.println("=== END OF REPORT ===");
          return true;
        }

        private void report(String description, SecurityContext securityContext) {
          StringBuilder output = new StringBuilder();

          output.append(description).append("\t");
          output.append(String.valueOf(securityContext)).append("\t");
          try {
            securityContext.isUserInRole("admin");
            output.append("SUCCESS");
          } catch (RuntimeException e) {
            output.append("FAILURE: ").append(e.getClass().getSimpleName()).append(" at ").append(e.getStackTrace()[0]);
          }
          System.out.println(output.toString());
        }
      })
    ;

    Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();

    return jsonb;
  }
}
