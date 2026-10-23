package com.yasinkanli.microservice.nginxlb.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;


public class LoadBalancerClient {

  private static final String DEFAULT_URL = "http://localhost:8080/hello";
  private static final int DEFAULT_REQUEST_COUNT = 20;

  public static void main(String[] args) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder(URI.create(DEFAULT_URL)).GET().build();
    Map<String, Integer> distribution = new TreeMap<>();

    for (int i = 1; i <= DEFAULT_REQUEST_COUNT; i++) {
      String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
      System.out.printf("#%d -> %s%n", i, body);
      distribution.merge(body, 1, Integer::sum);
    }

    System.out.println("\nDistribution:");
    distribution.forEach((response, hits) -> System.out.printf("%s : %d request%n", response, hits));
  }
}
