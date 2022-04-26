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
		
		return executeHttpRequestOnUBLBB();
	}

	// create http request at ubl bb health check endpoint
	private String executeHttpRequestOnUBLBB() {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest
				.newBuilder(URI.create("http://localhost:7080/rest-api/api/health"))
				.header("accept", "application/json").build();
		
		String retVal;
		
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

			retVal = response.statusCode() == 200 ? "Up" : "Down";
		}
		catch(IOException | InterruptedException ex) {
			retVal = "Down";
		}
		
		
		return retVal;
	}
}