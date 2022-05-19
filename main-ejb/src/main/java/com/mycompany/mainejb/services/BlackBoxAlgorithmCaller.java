package com.mycompany.mainejb.services;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ejb.Local;

import com.mycompany.shareddomain.dtos.DeviceDto;
import com.mycompany.shareddomain.dtos.ExecutionDto;


@Local
public interface BlackBoxAlgorithmCaller {
	ExecutionDto runManaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException;
	ExecutionDto runUnmanaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException;
}
