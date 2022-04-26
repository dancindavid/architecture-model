package com.mycompany.healthmonitor.services;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;


@Stateless
@Remote(HealthMonitor.class)
public class HealthMonitorService implements HealthMonitor {

	@Resource
	private ManagedScheduledExecutorService service;

	@Override
	public String getHealth() {
		
		String healthStatus = "";
		try {
			healthStatus = executeHttpRequestOnUBLBB();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return healthStatus;
	}

	// create http request at ubl bb health check endpoint
	private String executeHttpRequestOnUBLBB() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest
				.newBuilder(URI.create("http://localhost:7080/rest-api/api/health"))
				.header("accept", "application/json").build();
		

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

		return response.statusCode() == 200 ? "Up" : "Down";
	}
}