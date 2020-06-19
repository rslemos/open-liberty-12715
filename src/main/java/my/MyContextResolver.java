package my;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class MyContextResolver implements ContextResolver<Jsonb> {

  // somehow get this application injected
  private Application application;

  @Override
  public Jsonb getContext(Class<?> type) {
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
          try {
            SecurityContext securityContext = (SecurityContext)application.getProperties().get("securityContext");
            System.out.println("Using securityContext: " + securityContext);
            System.out.println("isUserInRole(admin): " + securityContext.isUserInRole("admin"));
            return true;
          } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
          }
        }
      })
    ;

    Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();

    return jsonb;
  }
}
