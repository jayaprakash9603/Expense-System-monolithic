package com.jaya.expenseservice.controller;

import com.jaya.userservice.modal.User;
import com.jaya.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("expenseHomeController")
public class HomeController {

	@Autowired
	private UserService userService;
	
	@GetMapping
	public String homeControllerHandler()
	{
		return "this is home controller";
	}
	
	@GetMapping("/home")
	public String homeControllerHandler2()
	{
		return "this is home controller 2";
	}

	@GetMapping("/test")
	public User testuserlogin(@RequestHeader("Authorization") String jwt) {
		User user = userService.getUserProfile(jwt);
		return user;
	}
}

