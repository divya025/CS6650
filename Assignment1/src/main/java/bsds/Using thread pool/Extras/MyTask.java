package bsds;

import java.util.concurrent.Callable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A task which can be passed to an Executor object.
 * When a task is called, it makes a specified number of HTTP requests and stores the results of these requests.
 */
public class MyTask implements Callable<Result> {

  private int numIterations;
  private String getServerURL;
  private String postServerURL;
  private Client client;
  private WebTarget webTarget;
  private Result result;

  public MyTask(int numIterations, String ip, int port) {
    this.numIterations = numIterations;
    this.getServerURL =
        ip.equals("http://localhost") ? ip + ":" + Integer.toString(port) + "/webapi/myresource"
            : ip + ":" + Integer.toString(port) + "/Assignment1_war/rest/myresource";
    this.postServerURL =
            ip.equals("http://localhost") ? ip + ":" + Integer.toString(port) + "/webapi/myresource/post"
                    : ip + ":" + Integer.toString(port) + "/Assignment1_war/rest/myresource/post";
    this.client = ClientBuilder.newClient();
    this.result = new Result();
  }

  private void makeGetRequest() {

    webTarget = client.target(getServerURL);
    Response response = null;
    String output = null;
    long start = System.currentTimeMillis();

    try {
      response = webTarget.request(MediaType.TEXT_PLAIN).get();
      output = response.readEntity(String.class);
      response.close();
    }
    catch (Exception e) {
      System.out.println("Problem making GET request");
    }

    long end = System.currentTimeMillis();
    result.incrementRequest();

    if(response != null && response.getStatus() == 200 && output.equals("Got it!")) {
      result.incrementSuccess();
      int latency = (int) (end - start);
      result.addLatency(latency);
    }
  }

  private void makePostRequest() {

    webTarget = client.target(postServerURL);
    Response response = null;
    long start = System.currentTimeMillis();
    Integer output = null;

    try {
      response = webTarget.request().post(Entity.text("hello"));
      output = response.readEntity(Integer.class);
      response.close();
    }
    catch (Exception e) {
      System.out.println("Post request failed: "+e);
    }
    long end = System.currentTimeMillis();
    result.incrementRequest();

    if(response != null && response.getStatus() == 200 && output == 5) {
      result.incrementSuccess();
      int latency = (int) (end - start);
      result.addLatency(latency);
    }
  }

  @Override
  public Result call() throws Exception {

    for (int i = 0; i < numIterations; i++) {
      makeGetRequest();
      makePostRequest();
    }

    client.close();
    return this.result;
  }
}
