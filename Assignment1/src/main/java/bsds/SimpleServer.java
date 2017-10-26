package bsds;

        import javax.ws.rs.Consumes;
        import javax.ws.rs.GET;
        import javax.ws.rs.POST;
        import javax.ws.rs.Path;
        import javax.ws.rs.Produces;
        import javax.ws.rs.core.MediaType;


@Path("simpleserver")
public class SimpleServer {

    // GET Method of the server
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return ("I am online now!");
    }

    //POST Method of the server
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public int postText(String content) {
        return (content.length());
    }


}