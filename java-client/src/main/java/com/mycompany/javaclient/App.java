package com.mycompany.javaclient;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mycompany.healthmonitor.services.NewYorkWeather;
import com.mycompany.shareddomain.dtos.DeviceDto;

import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * Hello world!
 *
 */
public class App {
	
	//private static final String jndiString = " java:global/health-monitor-ejb/NewYorkWeatherService!com.mycompany.healthmonitor.services.NewYorkWeather";
	private static final String jndiString = "ejb:/health-monitor-ejb/NewYorkWeatherService!com.mycompany.healthmonitor.services.NewYorkWeather";

	public static void main(String[] args) throws NamingException {

		NewYorkWeather newYorkWeather = lookup();
		System.out.println(newYorkWeather.getWeather());
		
	}

	private static NewYorkWeather lookup() throws NamingException {
		Properties jndiProperties = new Properties();
		jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
//        jndiProperties.put(Context.SECURITY_PRINCIPAL, "admin");
//        jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin");
		jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
		//jndiProperties.put(Context.URL_PKG_PREFIXES, "org.wildfly.naming.client");
		// jndiProperties.put("jboss.naming.client.ejb.context", true);
		
		
		final Context context = new InitialContext(jndiProperties);
//		NamingEnumeration<Binding> list = context.listBindings("");
//		while (list.hasMore()) {
//			Binding ncp = list.next();
//			ncp.setRelative(false);
//			System.out.println(ncp.toString());
//		}

		return (NewYorkWeather) context.lookup(jndiString);
	}
}