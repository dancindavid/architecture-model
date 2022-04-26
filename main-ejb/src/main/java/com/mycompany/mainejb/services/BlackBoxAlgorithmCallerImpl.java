package com.mycompany.mainejb.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.shareddomain.dtos.DeviceDto;
import com.mycompany.shareddomain.dtos.ExecutionDto;

@Stateless
@Local(BlackBoxAlgorithmCaller.class)
public class BlackBoxAlgorithmCallerImpl implements BlackBoxAlgorithmCaller {
	static final String baseString = "http://localhost:7080/rest-api/api/algorithm";

	
	@Override
	public ExecutionDto runManaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException {
				
		var objectMapper = new ObjectMapper();
		var deviceStr = objectMapper.writeValueAsString(device);
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder(URI.create(String.format("%s/%s/run-managed", baseString, algorithmKey)))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(deviceStr)).build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		if (response.statusCode() < 200 || response.statusCode() > 299) {
			throw new WebApplicationException(response.statusCode());
		}

		var body = response.body();

		ExecutionDto execution = objectMapper.readValue(body, ExecutionDto.class);

		return execution;		
	}

	@Override
	public ExecutionDto runUnmanaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException {
				
		var objectMapper = new ObjectMapper();
		var deviceStr = objectMapper.writeValueAsString(device);
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder(
				URI.create(String.format("%s/%s/run-unmanaged", baseString, algorithmKey)))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(deviceStr)).build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		if (response.statusCode() < 200 || response.statusCode() > 299) {
			throw new WebApplicationException(response.statusCode());
		}

		var body = response.body();

		ExecutionDto execution = objectMapper.readValue(body, ExecutionDto.class);

		return execution;		
	}
}
