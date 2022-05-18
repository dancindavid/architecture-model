package com.mycompany.unshareddomain.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionDto {
	UUID uuid;
	
	AlgorithmDto algorithm;
	
	DeviceDto device;
	DamageDto damage;

	boolean waiting;
	boolean done;
	boolean running;
}
