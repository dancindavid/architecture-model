package com.mycompany.mainejb.services;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.ejb.Remote;

import com.mycompany.shareddomain.dtos.DeviceDto;
import com.mycompany.shareddomain.dtos.ExecutionDto;


@Remote
public interface BlackBoxAlgorithmCaller {
	ExecutionDto runManaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException;
	ExecutionDto runUnmanaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException;
}
