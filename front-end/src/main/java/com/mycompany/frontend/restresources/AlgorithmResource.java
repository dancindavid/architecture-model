package com.mycompany.frontend.restresources;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mycompany.frontend.services.BlackBoxTester;
import com.mycompany.mainejb.services.BlackBoxAlgorithmCaller;
import com.mycompany.shareddomain.dtos.DeviceDto;


@RequestScoped
@Path("/algorithm")
@Produces("application/json")
@Consumes("application/json")
public class AlgorithmResource {
	
	private static final String jndiString = "java:global/main-ejb-jar-with-dependencies/BlackBoxAlgorithmCallerImpl!com.mycompany.mainejb.services.BlackBoxAlgorithmCaller";
	
	@Inject
	private BlackBoxTester blackBoxTester;
	
	private DeviceDto device = DeviceDto.builder().count(100000000).value(2).build();
//	private DeviceDto device = DeviceDto.builder().count(1).value(2).build();
	
	
	@GET
	@Path("/run-managed")
	public Response runAlgorithmManaged() {
		Long totalElapsedTime;
		
		try {
			totalElapsedTime = blackBoxTester.runManagedAlgorithmExecutions("sqrt", device);
		}
		catch(Exception e) {
			return Response.status(500).build();
		}
		
		return Response.ok(totalElapsedTime).build();
	}
	
	@GET
	@Path("/run-unmanaged")
	public Response runAlgorithmUnmanaged() {
		Long totalElapsedTime;
		
		try {
			totalElapsedTime = blackBoxTester.runUnmanagedAlgorithmExecutions("sqrt", device);
		}
		catch(Exception e) {
			return Response.status(500).build();
		}
		
		return Response.ok(totalElapsedTime).build();
	}
	
	private static BlackBoxAlgorithmCaller lookup() throws NamingException {
		final Context context = new InitialContext();
		
		return (BlackBoxAlgorithmCaller) context.lookup(jndiString);
	}

}
