package com.storage.bavifour.security.controller;

import com.storage.bavifour.security.exception.EmailAlreadyExistsException;
import com.storage.bavifour.security.exception.PasswordMismatchException;
import com.storage.bavifour.security.exception.UsernameAlreadyExistsException;
import com.storage.bavifour.security.model.User;
import com.storage.bavifour.security.model.UserRegistrationForm;
import com.storage.bavifour.security.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping({"/login", "/registration"})
    public String showAuthPage(Model model) {
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserRegistrationForm());
        }
        return "auth";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") @Valid UserRegistrationForm userForm,
                          BindingResult bindingResult,
                          Model model) {
        model.addAttribute("registerTab", true);

        if (bindingResult.hasErrors()) {
            return "auth";
        }


        try {
            if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
                model.addAttribute("passwordError", "Passwords do not match");
                throw new PasswordMismatchException("Passwords do not match");
            }

            if (userService.findByUsername(userForm.getUsername()) != null) {
                model.addAttribute("usernameError", "User with this username already exists");
                throw new UsernameAlreadyExistsException("User with this username already exists");
            }

            if (userService.findByEmail(userForm.getEmail()) != null) {
                model.addAttribute("emailError", "User with this email already exists");
                throw new EmailAlreadyExistsException("User with this email already exists");
            }

            User user = new User();
            user.setUsername(userForm.getUsername());
            user.setEmail(userForm.getEmail());
            user.setPassword(userForm.getPassword());

            userService.save(user);
            return "redirect:/login";
        } catch (PasswordMismatchException | UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
            return "auth";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred");
            logger.error("Unexpected error during registration", e);
            return "auth";
        }
    }
}
