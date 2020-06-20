package my;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Provider
public class MyContextResolver implements ContextResolver<ObjectMapper> {

  // somehow get this securityContext injected
  private SecurityContext securityContext;

  @Override
  public ObjectMapper getContext(Class<?> type) {
    SimpleFilterProvider filters = new SimpleFilterProvider();
    filters.addFilter("myfilter", new SimpleBeanPropertyFilter() {
        protected boolean include(BeanPropertyWriter writer) {
          return useSecurityContextToVetProperty();
        }

        protected boolean include(PropertyWriter writer) {
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
    });

    return new ObjectMapper().setFilterProvider(filters);
  }
}
