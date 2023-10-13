package com.contact.Controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.Helper.Message;
import com.contact.Repository.ContactRepository;
import com.contact.Repository.UserRepository;
import com.contact.entities.Contact;
import com.contact.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println("USERNAME :" + username);

		User user = userRepository.getUserByUserName(username);

		System.out.println("User : " + user);

		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";

	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("ProfileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			if (file.isEmpty()) {
				System.out.println("Image is Empty");
				contact.setImage("profile.png");
			

			} else {
				// upload file and update name of file
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Uploaded..!");
			}
			contact.setUser(user);

			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("DATA :" + contact);
			System.out.println("Added to DATABASE");
			session.setAttribute("message", new Message("Contact Added Successfully !!", "success"));

		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong..! " + e.getMessage(), "danger"));
		}
		return "normal/add_contact_form";
	}

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "Show User Contacts");

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		Pageable  pageable = PageRequest.of(page, 2);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";

	}
	
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		System.out.println("CID : "+cId);
		
		Optional<Contact> contact = this.contactRepository.findById(cId);
		Contact contact2 = contact.get();
		String userName=principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact2.getUser().getId()) {
			model.addAttribute("contact", contact2);
			model.addAttribute("title", contact2.getName());
		}
			
		return "normal/contact_details";
	}
//	
//	@GetMapping("/delete/{cId}")
//	public String deleteContact(@PathVariable("cId") Integer cId,Model model,HttpSession session) {
//		
//		Contact contact=this.contactRepository.findById(cId).get();
//		
//		System.out.println("Contact"+contact.getcId());
//		
//		contact.setUser(null);
//	    this.contactRepository.delete(contact);
//	
//		System.out.println("DELETED...");
//	session.setAttribute("message", new Message("Contact Deleted Successfully..!","success"));
//		return "redirect:/user/show-contacts/0";
//	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession session,Principal principal) {
	    Contact contact = this.contactRepository.findById(cId).get();
	    User user=this.userRepository.getUserByUserName(principal.getName());
	    user.getContacts().remove(contact);
	    this.userRepository.save(user);
	    System.out.println("DELETED...");
	//    session.setAttribute("message", new Message("Contact Deleted Successfully..!", "success"));
	    return "redirect:/user/show-contacts/0";
	}
	
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId,Model model) {
		model.addAttribute("title","Update Contact");
		Contact contact=this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}


}
