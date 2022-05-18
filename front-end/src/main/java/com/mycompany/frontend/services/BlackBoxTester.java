package com.mycompany.frontend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.wildfly.naming.client.WildFlyInitialContextFactory;

import com.mycompany.mainejb.services.BlackBoxAlgorithmCaller;
import com.mycompany.mainejb.services.BlackBoxExecutionCaller;
import com.mycompany.unshareddomain.dtos.DeviceDto;
import com.mycompany.unshareddomain.dtos.ExecutionDto;

@Stateless
public class BlackBoxTester {

	@EJB //(name = "ejb:/main-ejb/BlackBoxAlgorithmCallerImpl!com.mycompany.mainejb.services.BlackBoxAlgorithmCaller")
	private BlackBoxAlgorithmCaller algorithmCaller;

	@EJB //(name = "ejb:/main-ejb/BlackBoxExecutionCallerImpl!com.mycompany.mainejb.services.BlackBoxExecutionCaller")
	private BlackBoxExecutionCaller executionCaller;

	@Resource
	private ManagedExecutorService executorService;
	
//	public BlackBoxTester() throws NamingException {
//		algorithmCaller = lookupAlgorithm();
//		executionCaller = lookupExecution();
//	}

	private static BlackBoxAlgorithmCaller lookupAlgorithm() throws NamingException {
		Properties jndiProperties = new Properties();
		jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
	
		final Context context = new InitialContext(jndiProperties);
		
		var jndiString = "ejb:/main-ejb/BlackBoxAlgorithmCallerImpl!com.mycompany.mainejb.services.BlackBoxAlgorithmCaller";

		return (BlackBoxAlgorithmCaller) context.lookup(jndiString);
	}

	private static BlackBoxExecutionCaller lookupExecution() throws NamingException {
			Properties jndiProperties = new Properties();
			jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
			jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
		
			final Context context = new InitialContext(jndiProperties);
			
			var jndiString = "ejb:/main-ejb/BlackBoxExecutionCallerImpl!com.mycompany.mainejb.services.BlackBoxExecutionCaller";

			return (BlackBoxExecutionCaller) context.lookup(jndiString);
	}

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
