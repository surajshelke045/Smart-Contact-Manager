package com.contact.Controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contact.Repository.UserRepository;
import com.contact.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		
		String username =principal.getName();
		System.out.println("USERNAME :"+username);
		
		User user=userRepository.getUserByUserName(username);
		
		System.out.println("User : "+user);
		
		model.addAttribute("user",user);
		
		return "normal/user_dashboard";
	}

}
