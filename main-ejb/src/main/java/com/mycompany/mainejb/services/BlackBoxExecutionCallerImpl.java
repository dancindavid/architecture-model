package com.mycompany.mainejb.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.WebApplicationException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.shareddomain.dtos.DeviceDto;
import com.mycompany.shareddomain.dtos.ExecutionDto;

@Stateless
@Local(BlackBoxExecutionCaller.class)
public class BlackBoxExecutionCallerImpl implements BlackBoxExecutionCaller {
	static final String baseString = "http://localhost:7080/rest-api/api/execution";
	
	@Resource
	ManagedExecutorService executorService;
	
	@Override
	public Iterable<ExecutionDto> findAll() 
			throws IOException, InterruptedException, ExecutionException {
		
		var mapper = new ObjectMapper();
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder(URI.create(baseString))
				.header("Content-Type", "application/json")
				.GET().build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() > 299) {
			throw new WebApplicationException(response.statusCode());
		}

		var body = response.body();

		List<ExecutionDto> executions = mapper.readValue(body, new TypeReference<List<ExecutionDto>>(){});

		return executions;		
	}
	
	@Override
	public Optional<ExecutionDto> findById(UUID id) 
			throws IOException, InterruptedException, ExecutionException {
		var mapper = new ObjectMapper();
		var client = HttpClient.newHttpClient();
		var request = HttpRequest.newBuilder(
				URI.create(String.format("%s/%s", baseString, id.toString())))
				.header("Content-Type", "application/json")
				.GET().build();
		
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		if (response.statusCode() < 200 || response.statusCode() > 299) {
			throw new WebApplicationException(response.statusCode());
		}

		var body = response.body();
		if(response.body().isBlank()) {
			return Optional.empty();
		}
		
		var execution = mapper.readValue(body, ExecutionDto.class);

		return Optional.of(execution);		
	}
	
	@Override
	public void deleteAll() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder(
				URI.create(baseString + "/all"))
				.header("Content-Type", "application/json")
				.DELETE().build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		if (response.statusCode() < 200 || response.statusCode() > 299) {
			throw new WebApplicationException(response.statusCode());
		}
	}

//	@Override
//	public Iterable<ExecutionDto> findAllById(Iterable<UUID> ids)
//			throws IOException, InterruptedException, ExecutionException {
//		var mapper = new ObjectMapper();
//		var client = HttpClient.newHttpClient();
//		var request = HttpRequest.newBuilder(
//				URI.create(String.format("%s/%s", baseString, id.toString())))
//				.header("Content-Type", "application/json")
//				.GET().build();
//		
//		
//		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//		
//		if (response.statusCode() < 200 || response.statusCode() > 299) {
//			throw new WebApplicationException(response.statusCode());
//		}
//
//		var body = response.body();
//		var execution = mapper.readValue(body, ExecutionDto.class);
//
//		return execution;		
//	}

	
}
