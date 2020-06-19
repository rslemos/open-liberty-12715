package my;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/myresource")
@Produces(MediaType.APPLICATION_JSON)
public class MyResource {

  @GET
  public MyData getMyData() {
    return new MyData();
  }

  public static class MyData {
    public int myField = 42;
  }
}

