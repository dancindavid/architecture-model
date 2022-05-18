package com.mycompany.mainejb.services;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ejb.Remote;

import com.mycompany.unshareddomain.dtos.DeviceDto;
import com.mycompany.unshareddomain.dtos.ExecutionDto;


@Remote
public interface BlackBoxAlgorithmCaller {
	ExecutionDto runManaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException;
	ExecutionDto runUnmanaged(String algorithmKey, DeviceDto device) 
			throws IOException, InterruptedException, ExecutionException;
}
