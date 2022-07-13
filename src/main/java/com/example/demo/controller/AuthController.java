package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
import static com.example.demo.model.Status.BANNED;

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
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/registration")
    public String getRegisterPage() {
        return "registration";
    }

    @PostMapping("/error")
    public String loginError(@ModelAttribute("username") String username,
                             Model model) {
        User user = userRepository.findByEmail(username);
        if (user != null) {
            if (user.getStatus() == BANNED) {
                model.addAttribute("loginError", "Пользователь заблокирован");
            } else {
                model.addAttribute("loginError", "Неверный пароль");
            }
        } else {
            model.addAttribute("loginError", "Неверный логин");
        }
        return "login";
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

