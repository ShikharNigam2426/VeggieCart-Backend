package com.Veggie.Cart.ServiceInt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.Veggie.Cart.Entity.Register;

public interface RegisterInterface {
	public int getOtp(String email);

	public ResponseEntity<String> registerUser(Register register);

	public ResponseEntity<String> sendSimpleEmail(String from, String to, int message, String subject);

	public ResponseEntity<String> checkExistence(@RequestBody String email);

//	public boolean checkReset(@RequestBody String email);
//	
//	public boolean   sendResetMail(String email);
//	
//	public int sendResetOtp(@RequestBody String email);
//	
//	public boolean resetPassword(@RequestBody Register reset);
//	
//	public ResponseEntity<String> sendResetMail(String from, String to, int message, String subject);
}
