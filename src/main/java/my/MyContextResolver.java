package my;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class MyContextResolver implements ContextResolver<Jsonb> {

  private SecurityContext securityContext;

  @Context
  public void setSecurityContext(SecurityContext securityContext) {
    this.securityContext = securityContext;
  }

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
