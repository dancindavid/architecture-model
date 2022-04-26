package com.mycompany.mainejb.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.mycompany.shareddomain.dtos.ExecutionDto;



public interface BlackBoxExecutionCaller {
	Iterable<ExecutionDto> findAll() 
			throws IOException, InterruptedException, ExecutionException;
	Optional<ExecutionDto> findById(UUID id) 
			throws IOException, InterruptedException, ExecutionException;
//	Iterable<ExecutionDto> findAllById(Iterable<UUID> ids)
//			throws IOException, InterruptedException, ExecutionException;
	void deleteAll() 
			throws IOException, InterruptedException, ExecutionException;
}
