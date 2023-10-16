package com.contact.Controllers;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.Helper.Message;
import com.contact.Repository.UserRepository;
import com.contact.Service.EmailService;
import com.contact.entities.User;

@Controller
public class ForgotController {
	
	Random random=new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email ,HttpSession session) {
		System.out.println("Email : "+email);
		//generating  OPT of 4 digits
		
		
		int otp = random.nextInt(999999);
		System.out.println("OTP : "+otp);
		
		String subject="OTP from SCM";
		String message=""
				+"<div style='border:1px solid #e2e2e2; padding:20px;'>"
				+"<h1>"
				+"OTP is "
				+"<b>"+otp
				+"</b>"
				+ "</h1>"
				+ "</div>";
		String to=email;
		
		boolean flag=this.emailService.sendEmail(subject, message, to);
		
		 if(flag) {
			 
			 session.setAttribute("myotp", otp);
			 session.setAttribute("email", email);
			 return "verify_otp"; 

		 }
		 else {
			 
			 session.setAttribute("message","Check your email id !!");
			 return "forgot_email_form";
		 }

	}
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		int myOtp=(int) session.getAttribute("myotp");
		String email=(String) session.getAttribute("email");
		if(myOtp==otp)
		{
			User user = this.userRepository.getUserByUserName(email);
			if(user==null)
			{
			session.setAttribute("message","User does not found with this email !!");
			//session.setAttribute("message", new Message("User does not found with this email !!","danger"));
				 return "forgot_email_form";
			}
			else {
				 
			}
			return "password_change_form";
		}else {
			session.setAttribute("message","You have entered wrong OTP");
			return "verify_otp";
		}
		
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		
		String email=(String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		
		return "redirect:/signin?change=password changed successfully..";
	}
}