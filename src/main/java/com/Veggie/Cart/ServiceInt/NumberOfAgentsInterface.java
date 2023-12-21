package com.Veggie.Cart.ServiceInt;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.Veggie.Cart.Entity.Agent;

public interface NumberOfAgentsInterface {
	public List<Agent> fetchNumberOfAgents();
	public ResponseEntity<String> bootAgentMap(int numberOfAccounts);
}
