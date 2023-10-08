package com.contact.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.Repository.UserRepository;
import com.contact.entities.User;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home -SmartContact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About -SmartContact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register -SmartContact Manager");
		return "signup";
	}

}
