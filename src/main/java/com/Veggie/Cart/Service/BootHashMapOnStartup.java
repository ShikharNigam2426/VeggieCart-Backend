package com.Veggie.Cart.Service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Veggie.Cart.Dao.AccountsRepo;
import com.Veggie.Cart.Dao.RegisterDao;
import com.Veggie.Cart.Entity.Accounts;
import com.Veggie.Cart.Entity.Register;
import com.Veggie.Cart.ServiceInt.AccountsInterface;

@Service
public class BootHashMapOnStartup implements AccountsInterface {
	@Autowired
	RegisterDao register;
	@Autowired
	AccountsRepo accounts;

	static HashMap<String, String> mapUsernamePassword = new HashMap<>();
	@Override
	public List<Accounts> fetchNumberOfAccounts() {
		return this.accounts.findAll();
	}

	public List<Register> getRegisterData() {
		return this.register.findAll();
	}

	@Override
	public ResponseEntity<String> bootMap(int numberOfAccounts) {
//		if (!mapUsernamePassword.isEmpty()) {
//			System.out.println("hm-->"+mapUsernamePassword);
//			return ResponseEntity.status(200).body("Data already present");
//		} 
		System.out.println("Inside service"+mapUsernamePassword.size());
		if (numberOfAccounts == mapUsernamePassword.size())
			return ResponseEntity.status(304).body("Data already present in HashMap");
		List<Register> data = getRegisterData();
		if (data != null) {
			for (Register register : data) {
				if(register.getPassword().length()>1) {
				String username = register.getEmail();
				String password = register.getPassword();
				if (!mapUsernamePassword.containsKey(username))
					mapUsernamePassword.put(username, password);
			}
			}
			return ResponseEntity.status(200).body("Data filled in HashMap");
		}
		return ResponseEntity.status(204).body("No data present in Database");
	}	
	@Override
	public void updateMapWithNewRegistration(Register newRegister) {
        String username = newRegister.getEmail();
        String password = newRegister.getPassword();
        if (!mapUsernamePassword.containsKey(username)) {
            mapUsernamePassword.put(username, password);
        }
        System.out.println("Map"+mapUsernamePassword);
    }
//
//	@Override
//	public HashMap<String, String> sendMapToOtherService() {
//		return this.mapUsernamePassword;
//	}
}