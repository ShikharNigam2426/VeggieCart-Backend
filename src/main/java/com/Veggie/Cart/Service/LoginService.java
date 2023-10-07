package com.Veggie.Cart.Service;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	int isLoggedIn=0;
	String emailOfLoggedInUser="";
	@Override
	public ResponseEntity<String> loginUser(Login login) {
		Optional<Register> existUser = register.findById(login.getEmail());
		 if (!existUser.isPresent()) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
		    }
		int isPasswordWrong=0,isEmailCorrect=0;
		if (existUser.isPresent()) {
			Register existingUser = existUser.get();
			System.out.println("name"+existingUser.getName());
			if(existingUser.getVerified()==0) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not verified");
			}
			if(login.getEmail().equals(existingUser.getEmail())){
			 isEmailCorrect=1;
			if (login.getPassword().equals(existingUser.getPassword())){
				isPasswordWrong=1;
				isLoggedIn=1;
				emailOfLoggedInUser=login.getEmail();
				profile=new Register(existingUser.getEmail(),"Login Successfull",0,0,existingUser.getName(),existingUser.getAddress());
				return ResponseEntity.status(HttpStatus.OK).body("Login Successfull");
			}
			}
			else {
				System.out.println("Value of email is "+isEmailCorrect+"Value of password is "+isPasswordWrong);
				if(isEmailCorrect==0) {
					
					profile=new Register("","",0,0,"","Either password or email is wrong");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Email");
				}
				}
		}
		if(isPasswordWrong==0&&isEmailCorrect==0) {
			profile=new Register("","",0,0,"","Either password or email is wrong");
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong email and password");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Password");
	}

	@Override
	public ResponseEntity<Register> userProfile() {
		 return ResponseEntity.status(HttpStatus.OK).body(profile);
	}

	@Override
	public ResponseEntity<Register> loginInfo() {
 		if(isLoggedIn==1) {
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
    	 emailOfLoggedInUser="";		
    	 System.out.println("Data of logout clear is "+emailOfLoggedInUser);
    	 return ResponseEntity.status(HttpStatus.OK).body("user email id logged out and data cleared");
     }
     catch(Exception e) {
    	 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
     }
		
	}

	@Override
	public ResponseEntity<String> checkEmail() {
                 	  if(emailOfLoggedInUser.length()==0) {
        		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("not logged in");
        	  }
         
        	  return ResponseEntity.status(HttpStatus.OK).body("logged in");
          
	}
}
