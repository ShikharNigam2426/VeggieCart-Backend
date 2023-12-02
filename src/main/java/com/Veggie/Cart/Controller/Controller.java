package com.Veggie.Cart.Controller;
import com.Veggie.Cart.Service.BootHashMapOnStartup;
import java.util.List;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Veggie.Cart.Entity.Accounts;
import com.Veggie.Cart.Entity.Cart;
import com.Veggie.Cart.Entity.Issue;
import com.Veggie.Cart.Entity.Login;
import com.Veggie.Cart.Entity.Register;
import com.Veggie.Cart.Entity.Reset;
import com.Veggie.Cart.Entity.Vegetables;
import com.Veggie.Cart.ServiceInt.AccountsInterface;
import com.Veggie.Cart.ServiceInt.CartInterface;
import com.Veggie.Cart.ServiceInt.ForgotPassword;
import com.Veggie.Cart.ServiceInt.LoginInterface;
import com.Veggie.Cart.ServiceInt.RegisterInterface;
import com.Veggie.Cart.ServiceInt.ResolveIssue;
import com.Veggie.Cart.ServiceInt.VegetableInterface;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
	@Autowired
	RegisterInterface registeruser;
	@Autowired
	LoginInterface loginuser;
	@Autowired
	ForgotPassword forgot;
	@Autowired
	VegetableInterface vegetables;
	@Autowired
	ResolveIssue issue;
	@Autowired
	CartInterface cart;
	@Autowired
	AccountsInterface map;

	@GetMapping("/BootHashMap")
    public ResponseEntity<String> bootHashMap(){
		List<Accounts> list=map.fetchNumberOfAccounts();
		int numberOfAccounts=list.get(0).getNumberOfAccounts();
    	return map.bootMap(numberOfAccounts);
    }
	@PostMapping("/pushVegetables")
	public ResponseEntity<String> insertDataInVegetablesDao(@RequestBody List<Vegetables> vegetablesData) {
		return this.vegetables.saveVegetable(vegetablesData);
	}

	@GetMapping("/getVegetables")
	public List<Vegetables> getVegetables() {
		return this.vegetables.getVegetable();
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody Register register) {
		return this.registeruser.registerUser(register);
	}

	@PostMapping("/checkValidity")
	public ResponseEntity<String> checkExistence(@RequestBody String email) {
		return this.registeruser.checkExistence(email);
	}

	@PostMapping("/sendOTP")
	public ResponseEntity<String> sendOTP(@RequestBody String email) throws MessagingException {
		int otp = this.registeruser.getOtp(email);

		if (otp == -999999) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already registered");
		}

		return this.registeruser.sendSimpleEmail("madhurnigam96@gmail.com", email, otp,
				"Veggie Cart Email Verification OTP");
	}

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody Login login) {
		return this.loginuser.loginUser(login);
	}

	@GetMapping("/profile")
	public ResponseEntity<Register> profileData() {
		return this.loginuser.userProfile();
	}

	/**
	 * checkReset(string email) , resetPassword(@RequestBody Register reset) these
	 * two methods are used to reset user password
	 * 
	 * @param email
	 * @return ResponseEntity of String type
	 */
	@PostMapping("/checkReset")
	public ResponseEntity<String> checkReset(@RequestBody String email) {
		if (forgot.checkReset(email)) {
			if (forgot.checkSendResetMail(email)) {
				return new ResponseEntity<>("Reset Password OTP Sent", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Sending otp failed", HttpStatus.CONFLICT);
			}
		} else {
			return new ResponseEntity<>("User Does not exist", HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestBody Reset reset) {

		if (forgot.resetPassword(reset))
			return new ResponseEntity<>("Reset Successful", HttpStatus.OK);

		return new ResponseEntity<>("WRONG OTP", HttpStatus.BAD_REQUEST);

	}

	@PostMapping("/support")
	public ResponseEntity<String> raiseTicket(@RequestBody Issue ticket) {
		return this.issue.registerIssue(ticket);
	}

	@GetMapping("/getTicketInfo")
	public ResponseEntity<List<Issue>> getIssueList() {
		return this.issue.getIssueList(this.loginuser.getLoggedInEmail());
	}

	@GetMapping("/isLoggedIn")
	public ResponseEntity<Register> loginInfo() {
		return this.loginuser.loginInfo();
	}

	@PostMapping("/addToCart")
	public ResponseEntity<String> addToCart(@RequestBody Cart cart) {
		System.out.println(cart);
		return this.cart.addToCart(cart);
	}

	@GetMapping("/getCart")
	public ResponseEntity<List<Cart>> getCartList() {
		String emailOfLoggedInUser = this.loginuser.getLoggedInEmail();
		System.out.print("this is email of logged in user" + emailOfLoggedInUser);
		return this.cart.getCart(emailOfLoggedInUser);
	}

	@DeleteMapping("/deleteCartItem")
	public ResponseEntity<String> deleteCartItem(@RequestBody Cart item) {
		System.out.println("this is name" + item);
		return this.cart.deleteCartItem(item);
	}

	@PostMapping("/searchVegetable")
	public ResponseEntity<List<Vegetables>> searchVegetable(@RequestBody String name) {
		return this.vegetables.searchVegetble(name);
	}

	@GetMapping("/logoutDataClear")
	public ResponseEntity<String> logoutDataClear() {
		return this.loginuser.logoutDataClear();
	}

	@GetMapping("/checkEmail")
	public ResponseEntity<String> checkEmail() {
		return this.loginuser.checkEmail();
	}

}