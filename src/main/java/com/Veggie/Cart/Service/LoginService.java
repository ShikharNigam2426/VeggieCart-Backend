package com.Veggie.Cart.Service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.Veggie.Cart.Dao.RegisterDao;
import com.Veggie.Cart.Entity.Login;
import com.Veggie.Cart.Entity.Register;
import com.Veggie.Cart.ServiceInt.LoginInterface;

@Service
public class LoginService implements LoginInterface {
	@Autowired
	RegisterDao register;

	Register profile;
	int isLoggedIn = 0;
	String emailOfLoggedInUser = "";
	int isPasswordWrong = 0, isEmailCorrect = 0;

	@Override
	public ResponseEntity<String> loginUser(Login login) {
		if (!BootHashMapOnStartup.mapUsernamePassword.containsKey(login.getEmail()))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
		else if (BootHashMapOnStartup.mapUsernamePassword.containsKey(login.getEmail())) {
			isEmailCorrect = 1;
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			String password = bcrypt.encode(login.getPassword());
			System.out.println("Passowrd"+password);
			System.out.println("@@@@@@@@@"+BootHashMapOnStartup.mapUsernamePassword.get(login.getEmail()));
			if(password.equals(BootHashMapOnStartup.mapUsernamePassword.get(login.getEmail())))
				System.out.println("true");
			if (bcrypt.matches(login.getPassword(),BootHashMapOnStartup.mapUsernamePassword.get(login.getEmail()))) {
				isPasswordWrong = 1;
				isLoggedIn = 1;
				emailOfLoggedInUser = login.getEmail();
				Optional<Register> loggedIn = this.register.findById(login.getEmail());
				profile = new Register(login.getEmail(), "Login Successfull", 0, 0, loggedIn.get().getName(),
						loggedIn.get().getAddress());
				return ResponseEntity.status(HttpStatus.OK).body("Login Successfull");
			} 
			else if (isEmailCorrect == 0) {
//				profile = new Register("", "", 0, 0, "", "Either password or email is wrong");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Email");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Password");
	}

	@Override
	public ResponseEntity<Register> userProfile() {
		return ResponseEntity.status(HttpStatus.OK).body(profile);
	}

	@Override
	public ResponseEntity<Register> loginInfo() {
		if (isLoggedIn == 1) {
			return ResponseEntity.status(HttpStatus.OK).body(profile);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}

	@Override
	public String getLoggedInEmail() {
		return emailOfLoggedInUser;

	}

	@Override
	public ResponseEntity<String> logoutDataClear() {
		try {
			emailOfLoggedInUser = "";
			System.out.println("Data of logout clear is " + emailOfLoggedInUser);
			return ResponseEntity.status(HttpStatus.OK).body("user email id logged out and data cleared");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}

	}

	@Override
	public ResponseEntity<String> checkEmail() {
		if (emailOfLoggedInUser.length() == 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("not logged in");
		}

		return ResponseEntity.status(HttpStatus.OK).body("logged in");

	}
}
