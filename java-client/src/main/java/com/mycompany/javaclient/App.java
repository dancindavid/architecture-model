package com.mycompany.javaclient;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

//import org.jboss.naming.remote.client.InitialContextFactory;

import org.wildfly.naming.client.WildFlyInitialContextFactory;

import com.mycompany.healthmonitor.services.HealthMonitor;


public class App {
	private static final String jndiString = "ejb:architecture-model/health-monitor-ejb/HealthMonitorService!com.mycompany.healthmonitor.services.HealthMonitor";

	public static void main(String[] args) throws NamingException {
		HealthMonitor healthMonitor = lookup();
		System.out.println(healthMonitor.getHealth());
	}

	private static HealthMonitor lookup() throws NamingException {
		Properties jndiProperties = new Properties();
		jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
		jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
	
		final Context context = new InitialContext(jndiProperties);
		
		return (HealthMonitor) context.lookup(jndiString);
	}
}