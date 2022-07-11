package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
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

    private List<User> users = new ArrayList<>();

    private List<TDList> lists = new ArrayList<>();

    private List<Task> tasks = new ArrayList<>();

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

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null) {
            model.put("message", "Данный пользователь уже существует");
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
