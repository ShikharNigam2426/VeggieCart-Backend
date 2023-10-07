package com.Veggie.Cart.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.List;
import java.util.Optional;
import com.Veggie.Cart.Dao.RegisterDao;
import com.Veggie.Cart.Entity.*;
import com.Veggie.Cart.ServiceInt.RegisterInterface;

@Service
public class RegisterService implements RegisterInterface {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private RegisterDao register;
	int generatedOTP;

	public List<Register> getUserData() {
		return this.register.findAll();
	}
    @Override
	public ResponseEntity<String> registerUser(Register register) {
		try {
			if (this.register.findById(register.getEmail()).isPresent()) {
				if (register.getVerified() == 1)
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.body("An account with this email id already exist");
			}
			Optional<Register> existingRegister = this.register.findById(register.getEmail());
			int otp = 0;
			if (existingRegister.isPresent()) {
				// Update existing entity
				Register existingEntity = existingRegister.get();
                System.out.println("this is extracted from database"+existingEntity);
				otp = existingEntity.getOtp();
			}
			if (verifyOtp(otp, register.getOtp()) == 1) {
				register.setVerified(1);
				System.out.println(register.getVerified());
				System.out.println(register);
				this.register.save(register);
				return ResponseEntity.status(HttpStatus.OK).body("Registered Successfully");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error Registering User: Otp is invalid");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error Registering User: Otp is invalid" + e.getMessage());
		}
	}

	@Override
	public ResponseEntity<String> sendSimpleEmail(String from, String to, int message, String subject) {

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		String otp = String.valueOf(message);
		String messageSend = "Welcome to VeggieCart, your go-to destination for fresh and healthy produce delivered right to your doorstep!\n\n"
				+ "To ensure the security of your account and start shopping with us, please verify your email address by entering the OTP (One-Time Password) provided below:\n\n"
				+ "OTP: " + otp + "\n\n"
				+ "Please enter the above OTP in the verification field on our website to complete the registration process. This OTP will be valid for the next 10 minutes.\n\n"
				+ "Thank you for choosing VeggieCart. We're excited to have you on board!\n\n" + "Best regards,\n"
				+ "The VeggieCart Team";
		simpleMailMessage.setTo(to);
		simpleMailMessage.setText(messageSend);
		simpleMailMessage.setFrom(from);
		simpleMailMessage.setSubject(subject);

		mailSender.send(simpleMailMessage);
		return ResponseEntity.status(HttpStatus.OK).body("Otp sent");

	}

	@Override
	public int getOtp(String email) {
		int otpLength = 6; // Length of the OTP
		String otp = "";

		for (int i = 0; i < otpLength; i++) {
			int digit = 0;
			digit = (int) (Math.random() * 10); // Generate a random digit between 0 and 9
			otp += (digit);
		}
		generatedOTP = Integer.parseInt(otp);
		Optional<Register> existingRegister = register.findById(email);
		if (existingRegister.isPresent()) {
			// Update existing entity
			Register existingEntity = existingRegister.get();
			if (existingEntity.getVerified() == 1)
				return -999999;
		}

		Register newRegister = new Register(email, "", generatedOTP, 0,"","");
		this.register.save(newRegister);
		return generatedOTP;
	}

	public int verifyOtp(int generatedOTP, int userOtp) {
		String first = String.valueOf(generatedOTP);
		String second = String.valueOf(userOtp);
		for (int i = 0; i < first.length(); i++) {
			if (first.charAt(i) != second.charAt(i))
				return 0;
		}
		return 1;
	}

	@Override
	public ResponseEntity<String> checkExistence(String email) {
		Optional<Register> existingRegister = register.findById(email);
		if (existingRegister.isPresent()) {
			// Update existing entity
			Register existingEntity = existingRegister.get();
			if (existingEntity.getVerified() == 1)
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Account exist otp will not be sent");
		}
		return ResponseEntity.status(HttpStatus.OK).body("new user send otp to email");
	}

//	@Override
//	public boolean checkReset(String email) {
//		Optional<Register> exist = this.register.findById(email);
//		if (exist.isPresent()) {
//			Register reset = exist.get();
//			if (reset.getVerified() == 1)
//				return true;
//		}
//		return false;
//	}
//
//	@Override
//	public int sendResetOtp(String email) {
//		int otpLength = 6; // Length of the OTP
//		String otp = "";
//
//		for (int i = 0; i < otpLength; i++) {
//			int digit = 0;
//			digit = (int) (Math.random() * 10); // Generate a random digit between 0 and 9
//			otp += (digit);
//		}
//		generatedOTP = Integer.parseInt(otp);
//		Optional<Register> existingRegister = register.findById(email);
//		if (existingRegister.isPresent()) {
//			// Update existing entity
//			Register existingEntity = existingRegister.get();
//			if (existingEntity.getVerified() == 1) {
//				// Update existing entity with the new OTP
//				System.out.println(generatedOTP);
//				Register update = new Register(email, existingEntity.getPassword(), generatedOTP, 1);
//				this.register.save(update);
//				System.out.println("this is updated otp" + existingEntity.getOtp());
//			}
//		}
//		return generatedOTP;
//	}
//
//	@Override
//	public boolean resetPassword(Register resetObj) {
//		Optional<Register> reset = this.register.findById(resetObj.getEmail());
//		Register newEntity = reset.get();
//		if (reset.isPresent()) {
//			int resetPassword = reset.get().getOtp();
//			if (verifyOtp(resetPassword, resetObj.getOtp()) == 1) {
//				newEntity.setPassword(resetObj.getPassword());
//				register.save(newEntity);
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public ResponseEntity<String> sendResetMail(String from, String to, int message, String subject) {
//		System.out.println(to);
//
//		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//		String otp = String.valueOf(message);
//		String messageSend = "Hello,\n\n"
//				+ "You are receiving this email because you requested to reset your VeggieCart account password. To proceed with the password reset, please use the following OTP (One-Time Password):\n\n"
//				+ "OTP: " + otp + "\n\n"
//				+ "Please enter the above OTP on the password reset page within the next 10 minutes. If you didn't request this password reset, you can safely ignore this email.\n\n"
//				+ "If you have any questions or need assistance, please contact our support team.\n\n"
//				+ "Thank you for using VeggieCart!\n\n" + "Best regards,\n" + "The VeggieCart Team";
//		simpleMailMessage.setTo(to); // "to" should be the recipient's email address
//		simpleMailMessage.setText(messageSend);
//		simpleMailMessage.setFrom(from);
//		simpleMailMessage.setSubject(subject);
//
//		mailSender.send(simpleMailMessage);
//		return ResponseEntity.status(HttpStatus.OK).body("Password reset OTP sent");
//
//	}
//
//	@Override
//	public boolean sendResetMail(String email) {
//		try {
//			int otp = this.sendResetOtp(email);
//			this.sendResetMail("madhurnigam96@gmail.com", email, otp, "Reset Password for VeggieCart");
//			return true;
//		} catch (Exception ex) {
//			return false;
//		}
//
//	}
}