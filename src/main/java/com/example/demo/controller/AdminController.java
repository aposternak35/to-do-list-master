package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.model.Role.ADMIN;
import static com.example.demo.model.Role.USER;
import static com.example.demo.model.Status.ACTIVE;
import static com.example.demo.model.Status.BANNED;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    @Autowired
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private List<User> users = new ArrayList<>();

    @GetMapping("/listOfUsers")
    public String checkRole(Principal principal, Model model) {
        users.clear();
        users = userRepository.findAll();
        User user = userRepository.findByEmail(principal.getName());
        Role userRole = USER;
        Role adminRole = ADMIN;
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        model.addAttribute("userRole", userRole);
        model.addAttribute("adminRole", adminRole);
        return "listOfUsers";
    }

    @PostMapping("/changeRole")
    private String changeRole(User user) {
        User bufuser = userRepository.findByEmail(user.getEmail());
        if (user.getRole() == USER) {
            bufuser.setRole(ADMIN);
        } else {
            bufuser.setRole(USER);
        }
        userRepository.deleteById(user.getId());
        userRepository.save(bufuser);
        return "redirect:/admin/listOfUsers";
    }

    @PostMapping("/changeStatus")
    private String changeStatus(User user) {
        User bufuser = userRepository.findByEmail(user.getEmail());
        if (user.getStatus() == ACTIVE) {
            bufuser.setStatus(BANNED);
        } else {
            bufuser.setStatus(ACTIVE);
        }
        userRepository.deleteById(user.getId());
        userRepository.save(bufuser);
        return "redirect:/admin/listOfUsers";
    }
}
