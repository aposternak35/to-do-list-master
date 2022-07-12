package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static com.example.demo.model.Role.USER;
import static com.example.demo.model.Status.ACTIVE;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    @GetMapping("/login")
    public String getLoginPage(Model model,
                               @RequestParam(value = "error", required = false) String error) {
        if (checkAuth()) {
            return "redirect:/index";
        }

        if (error != null) {
            model.addAttribute("error", "Неверно введены данные или пользователь забанен");
        }
        return "login";
    }

    private boolean checkAuth() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User;
    }

    @GetMapping("/registration")
    public String getRegisterPage() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null) {
            model.put("message", "Пользователь с данным email уже существует");
            return "registration";
        } else {
            user.setPassword(passwordEncoder().encode(user.getPassword()));
            user.setRole(USER);
            user.setStatus(ACTIVE);
            userRepository.save(user);
            return "redirect:/auth/login";
        }
    }
}

