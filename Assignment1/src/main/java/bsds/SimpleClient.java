package bsds;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SimpleClient {

// Cyclic barrier object
static CyclicBarrier barrier;

private static void getClient() {
    Client client = ClientBuilder.newClient();
    // Test for local computer
    WebTarget webTarget = client.target("http://localhost:7070/webapi/simpleserver");

    // Test for ec2 instance
    //WebTarget webTarget = client.target("hhttp://ec2-34-214-91-35.us-west-2.compute.amazonaws.com:8080/Assignment1/webapi/simpleserver");

    System.out.println(webTarget.request(MediaType.TEXT_PLAIN).get(String.class));
}

private static void postClient() {
    Client client = ClientBuilder.newClient();
    //Test for local computer
    WebTarget webTarget = client.target("http://localhost:7070/webapi/simpleserver");

    // Test for ec2 instance
    //WebTarget webTarget = client.target("http://ec2-34-214-91-35.us-west-2.compute.amazonaws.com:8080/Assignment1/webapi/simpleserver");
    System.out.println(webTarget.request().post(Entity.text("HelloWorld")));
}

public static void main(String[] args) {

    // Multithreaded Java Client using Cyclic Barrier
    String hostName;
    int port;
    int MAX_THREADS = 10;

    if (args.length == 2) {
        hostName = args[0];
        port = Integer.parseInt(args[1]);
    } else {

        // set the defaults
        hostName = "localhost";
        port = 7070;
    }

    // Create a cyclic barrier to handle MAX_THREADS + 1 main thread
    barrier = new CyclicBarrier(MAX_THREADS + 1);


        /*  client 10 100 serverIPaddress 8080
        Client starting …..Time: nnnnn
        All threads running ….
        All threads complete …. Time: nnnnn
        Total number of requests sent: nnn
        Total number of Successful responses: nnnn
        Test Wall Time: nn.n seconds    */

    System.out.println("Client 10 100 Server-IPaddress 8080");
    System.out.print("Client Starting....");
    System.out.println("    Time:" + System.currentTimeMillis());
    System.out.println("All threads running....");

    SocketClientThreadEx clients[] = new SocketClientThreadEx[MAX_THREADS];
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < MAX_THREADS; i++) {
        clients[i] = new SocketClientThreadEx(hostName, port, barrier);
        clients[i].start();
    }

    try {
        System.out.println("!!!Main Thread waiting at barrier!!!!");
        barrier.await();
        System.out.println("Main thread Thread finishing");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.print("All threads complete....");
        System.out.println("    Time:" + System.currentTimeMillis());
        System.out.println("The total time is:" + totalTime);
    } catch (InterruptedException ex) {
        return;
    } catch (BrokenBarrierException ex) {
        return;
    }
 }
}
