package bsds;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;



public class SocketClientThreadEx extends Thread{
    
    private long clientID;
    private static int countGet =0;
    private static int countPost =0;
    String hostName;
    int port;
    CyclicBarrier synk;
    
    public SocketClientThreadEx(String hostName, int port, CyclicBarrier barrier) {
        this.hostName = hostName;
        this.port = port;
        clientID = Thread.currentThread().getId();
        synk = barrier;
    }
    
    private static void getClient() {
        Client client = ClientBuilder.newClient();
        // Test for local computer
        WebTarget webTarget = client.target("http://localhost:7070/webapi/simpleserver");
        
        // Test for ec2 instance
        //WebTarget webTarget = client.target("hhttp://ec2-34-214-91-35.us-west-2.compute.amazonaws.com:8080/Assignment1/webapi/simpleserver");
        
        String output = webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
        if(output!=null) {
            countGet++;
        }
        System.out.println(output);
    }
    
    private static void postClient() {
        Client client = ClientBuilder.newClient();
        //Test for local computer
        WebTarget webTarget = client.target("http://localhost:7070/webapi/simpleserver");
        
        // Test for ec2 instance
        //WebTarget webTarget = client.target("http://ec2-34-214-91-35.us-west-2.compute.amazonaws.com:8080/Assignment1/webapi/simpleserver");
        
        Response output = webTarget.request().post(Entity.text("HelloWorld"));
        if(output!=null) {
            countPost++;
        }
        System.out.println(output);
    }
    
    public void run() {
        long startTime = 0;
        try {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 100 ; i++ ) {
                Socket s = new Socket(hostName, port);
                
                PrintWriter out =
                new PrintWriter(s.getOutputStream(), true);
                BufferedReader in =
                new BufferedReader(
                                   new InputStreamReader(s.getInputStream()));
                
                out.println("Client ID is " +  Long.toString(clientID));
                System.out.println(in.readLine());
                
                // To find the latency step 5
                PrintWriter writer = new PrintWriter("fileGet.txt", "UTF-8");
                PrintWriter writer2 = new PrintWriter("filePost.txt", "UTF-8");;
                long sTime, eTime, tTime ;
                
                sTime = System.currentTimeMillis();
                getClient();
                eTime = System.currentTimeMillis();
                tTime = eTime - sTime;
                writer.println(tTime);
                
                sTime = System.currentTimeMillis();
                postClient();
                eTime = System.currentTimeMillis();
                tTime = eTime - sTime;
                writer2.println(tTime);
                
                s.close();
                
                s.close();
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                               hostName);
            System.exit(1);
        }
        finally{
            if (writer != null) {
                writer.close();
            }
            if (writer2 != null){
                writer2.close();
            }
        }
        try {
            System.out.println("!!!!Thread waiting at barrier!!!!") ;
            synk.await();
            System.out.println("Thread finishing");
            long endTime = System.currentTimeMillis();
            long totalTime = endTime-startTime;
            System.out.println("The time to run each post and get together is:" + totalTime);
            System.out.println("Successfull Get calls:"+countGet);
            System.out.println("Successfull Post calls:"+countPost);
        } catch (InterruptedException ex) {
            return;
        } catch (BrokenBarrierException ex) {
            return;
        }
        
    }
}
