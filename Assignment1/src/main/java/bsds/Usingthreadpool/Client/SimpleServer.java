//package bsds.Usingthreadpool.Client;
//
///**
// * Created by Divya 9/29/2017
// */
//
//
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//
//
//@Path("simpleserver")
//public class SimpleServer {
//
//    /**
//     * To retrieve a message
//     * @return a String
//     */
//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getStatus() {
//        return "I am on now!";
//    }
//
//    /**
//     *
//     * @param content is String
//     * @return length of input String content
//     */
//    @POST
//    @Path("/post")
//    @Consumes(MediaType.TEXT_PLAIN)
//    public int postText(String content)
//    {
//        return content.length();
//    }
//}
