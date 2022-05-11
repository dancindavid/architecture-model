package com.mycompany.healthmonitor.services;

import javax.ejb.Remote;

@Remote
public interface HealthMonitor {
	String getHealth();
}