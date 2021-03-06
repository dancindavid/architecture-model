package com.mycompany.frontend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;

import com.mycompany.mainejb.services.BlackBoxAlgorithmCaller;
import com.mycompany.mainejb.services.BlackBoxExecutionCaller;
import com.mycompany.shareddomain.dtos.DeviceDto;
import com.mycompany.shareddomain.dtos.ExecutionDto;

@Stateless
public class BlackBoxTester {

	@EJB 
	private BlackBoxAlgorithmCaller algorithmCaller;

	@EJB 
	private BlackBoxExecutionCaller executionCaller;

	@Resource
	private ManagedExecutorService executorService;

	public Long runManagedAlgorithmExecutions(String algorithmKey, DeviceDto device)
			throws IOException, InterruptedException, ExecutionException {

		Callable<ExecutionDto> callable = () -> algorithmCaller.runManaged(algorithmKey, device);

		List<ExecutionDto> executions = startExecutionsRunning(callable);

		List<UUID> executionIds = executions.stream().map(ExecutionDto::getUuid).collect(Collectors.toList());

		executions = waitUntilExecutionsDone(executionIds);

		Long totalElapsedTime = executions.stream().map(execution -> execution.getDamage().getElapsedTime()).reduce(0L,
				Long::sum);

		executionCaller.deleteAll();

		return totalElapsedTime;
	}

	public Long runUnmanagedAlgorithmExecutions(String algorithmKey, DeviceDto device)
			throws IOException, InterruptedException, ExecutionException {

		Callable<ExecutionDto> callable = () -> algorithmCaller.runUnmanaged(algorithmKey, device);

		List<ExecutionDto> executions = startExecutionsRunning(callable);

		List<UUID> executionIds = executions.stream().map(ExecutionDto::getUuid).collect(Collectors.toList());

		executions = waitUntilExecutionsDone(executionIds);

		Long totalElapsedTime = executions.stream().map(execution -> execution.getDamage().getElapsedTime()).reduce(0L,
				Long::sum);

		executionCaller.deleteAll();

		return totalElapsedTime;
	}

	private List<ExecutionDto> startExecutionsRunning(Callable<ExecutionDto> callable)
			throws InterruptedException, ExecutionException {

		List<Callable<ExecutionDto>> callables = new ArrayList<Callable<ExecutionDto>>();

		for (var i = 0; i < 10; ++i) {
			callables.add(callable);
		}

		List<Future<ExecutionDto>> executionFutures = executorService.invokeAll(callables);

		List<ExecutionDto> executions = new ArrayList<ExecutionDto>(executionFutures.size());
		for (var future : executionFutures) {
			ExecutionDto execution = future.get();
			executions.add(execution);
		}

		return executions;
	}

	private List<ExecutionDto> waitUntilExecutionsDone(List<UUID> ids) throws InterruptedException, ExecutionException {

		List<Callable<ExecutionDto>> executionCallables = new ArrayList<Callable<ExecutionDto>>();

		for (var id : ids) {
			executionCallables.add(() -> waitUntilExecutionDone(id));
		}

		List<Future<ExecutionDto>> executionFutures = executorService.invokeAll(executionCallables);

		List<ExecutionDto> executions = new ArrayList<ExecutionDto>();

		for (var future : executionFutures) {
			executions.add(future.get());
		}

		return executions;
	}

	private ExecutionDto waitUntilExecutionDone(UUID id) throws InterruptedException, ExecutionException {

		Future<Optional<ExecutionDto>> executionFuture = executorService.submit(() -> executionCaller.findById(id));
		Optional<ExecutionDto> execution = executionFuture.get();

		while ((execution.isPresent() && !execution.get().isDone()) || execution.isEmpty()) {
			Thread.sleep(2000);
			executionFuture = executorService.submit(() -> executionCaller.findById(id));
			execution = executionFuture.get();
		}

		return execution.get();
	}

}
