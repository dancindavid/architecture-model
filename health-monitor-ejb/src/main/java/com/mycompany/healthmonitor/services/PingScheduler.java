package com.mycompany.healthmonitor.services;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Startup
@Singleton
@LocalBean
public class PingScheduler {

	private static final String jndiGlobalString = "ejb:/healthmonitor/HealthMonitorService!com.mycompany.healthmonitor.services.HealthMonitor";

	@EJB
	private HealthMonitor healthMonitor;

	@Resource
	private ManagedScheduledExecutorService scheduler;
	
	private ScheduledFuture<?> scheduledFuture;

	@PostConstruct
	private void init() {
		scheduledFuture = scheduler.scheduleAtFixedRate(this::run, 0, 10, TimeUnit.SECONDS);
		//this.healthMonitor = healthMonitorSetup();
	}
	
	@PreDestroy
	private void preDestroy() {
		scheduledFuture.cancel(false);
	}
	
	private void run() {
		System.out.println(LocalDateTime.now());
		System.out.println(healthMonitor.getHealth());
	}

	private static HealthMonitor healthMonitorSetup() throws NamingException {
		Properties jndiProperties = new Properties();
		jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
		final Context context = new InitialContext(jndiProperties);

		return (HealthMonitor) context.lookup(jndiGlobalString);
	}
}