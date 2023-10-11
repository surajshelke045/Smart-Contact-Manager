package com.contact.Controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contact.Helper.Message;
import com.contact.Repository.UserRepository;
import com.contact.entities.Contact;
import com.contact.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String username =principal.getName();
		System.out.println("USERNAME :"+username);
		
		User user=userRepository.getUserByUserName(username);
		
		System.out.println("User : "+user);
		
		model.addAttribute("user",user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		
		model.addAttribute("title","User Dashboard");
		
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
		
	}
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,Principal principal,HttpSession session) {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		contact.setUser(user);

		user.getContacts().add(contact);
		session.setAttribute("message", new Message("Successfully Registered !!","alert-success" ));
		
		this.userRepository.save(user);
		
		System.out.println("DATA :"+contact);
		System.out.println("Added to DATABASE" );
		return "normal/add_contact_form";
	}

}
