package com.Veggie.Cart.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.Veggie.Cart.Dao.AgentRepo;
import com.Veggie.Cart.Entity.Agent;
import com.Veggie.Cart.ServiceInt.AgentInterface;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

@Service
public class AgentService implements AgentInterface {
	@Autowired
	AgentRepo agentRepo;

	static HashMap<Integer, Boolean> rejectedByAgent = new HashMap<>();

	@Override
	public ResponseEntity<String> saveAgentDetails(Agent agent) {
		try {
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			String encryptedPassword = bcrypt.encode(agent.getPassword());
			agent.setPassword(encryptedPassword);
			this.agentRepo.save(agent);
			BootHashMapOnStartup.mapAgentUsernamePassword.put(agent.getId(), encryptedPassword);
			System.out.println("AgentHashMap" + BootHashMapOnStartup.mapAgentUsernamePassword);
			return ResponseEntity.status(200).body("Agent Details Saved");
		} catch (Exception e) {
			System.out.println("Exception" + e);
			return ResponseEntity.status(500).body("Error ocurred while saving Agent data");
		}
	}

	public List<Agent> fetchAgents() {
		return this.agentRepo.findAll();
	}

	@Override
	public ResponseEntity<Agent> isAgentAvailaible() {
		Agent noAgent=new Agent(-9999,"null",false,"null",false);
		try {
			List<Agent> agentData = this.fetchAgents();
			if (agentData != null) {
				for (Agent agent : agentData) {
					if ((agent.getStatus())==true
							&&
						(agent.isChatting())==false) {
						return ResponseEntity.status(200).body(agent);
					}
				}
				return ResponseEntity.status(503)
						.body(noAgent);
			}
			return ResponseEntity.status(503).body(noAgent);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(noAgent);
		}
	}

	@Override
	public ResponseEntity<Agent> chatRejectedByAgent(int id, boolean rejected) {
		rejectedByAgent.put(id, rejected);
		Agent noAgent=new Agent(-9999,"null",false,"null",false);
		try {
			List<Agent> agentData = this.fetchAgents();
			if (agentData != null) {
				for (Agent agent : agentData) {
					if (!rejectedByAgent.containsKey(agent.getId())) {
						if ((agent.getStatus())==true) {
							return ResponseEntity.status(200).body(agent);
						}
					}
				}
				return ResponseEntity.status(503)
						.body(noAgent);
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body(noAgent);
		}
		return ResponseEntity.status(500).body(noAgent);
	}

	@Override
	public ResponseEntity<String> updateAgentStatusOnLogin(Agent agent) {
		try {
			Agent loggedInAgent=this.agentRepo.findById(agent.getId()).get();
			agent.setStatus(true);
			agent.setChatting(false);
			agent.setName(loggedInAgent.getName());
			this.agentRepo.save(agent);
			return ResponseEntity.status(200).body("Agent status Updated to True");
		}
		catch(Exception e) {
			return ResponseEntity.status(500).body("Internal Server Error");
		}
	}

	@Override
	public ResponseEntity<String> updateAgentStatusOnLogout(Agent agent) {
		try {
			Agent loggedInAgent=this.agentRepo.findById(agent.getId()).get();
			agent.setStatus(false);
			agent.setChatting(false);
			agent.setName(loggedInAgent.getName());
			this.agentRepo.save(agent);
			return ResponseEntity.status(200).body("Agent status Updated to False");
		}
		catch(Exception e) {
			return ResponseEntity.status(500).body("Internal Server Error");
		}
	}
}