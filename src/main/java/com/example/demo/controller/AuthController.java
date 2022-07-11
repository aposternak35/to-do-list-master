package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.ListRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.demo.model.Role.ADMIN;
import static com.example.demo.model.Role.USER;
import static com.example.demo.model.Status.ACTIVE;
import static com.example.demo.model.Status.BANNED;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    @Autowired
    @Resource
    private ListRepository listRepository;

    @Autowired
    @Resource
    private TaskRepository taskRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private List<TDList> lists = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    private List<User> users = new ArrayList<>();

    private String email;
    private String listId;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model, Principal principal) {
        lists.clear();
        email = principal.getName();
        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user", user);
        lists.addAll(listRepository.findByUserEmail(email));
        model.addAttribute("lists", lists);
        return "index";
    }


    @GetMapping("/registration")
    public String getRegisterPage() {
        return "registration";
    }

    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
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

    @PostMapping("/addList")
    public String addList(TDList list, Map<String, Object> model) {
        list.setUserEmail(email);
        listRepository.save(list);
        return "redirect:/auth/index";
    }

    @GetMapping("/addTask")
    public String getList(Model model) {
        tasks.clear();
//        listId=tdlist.getId().toString();
//        model.addAttribute("tdList",tdlist);
        tasks.addAll(taskRepository.findAll());
        model.addAttribute("tasks", tasks);
        return "addTask";
    }

    @PostMapping("/addTask")
    public String addTask(Task task, Map<String, Object> model) {
        task.setListId("1");
        taskRepository.save(task);
        return "redirect:/auth/addTask";
    }

    @PostMapping("/complete_check")
    public String checkComplete(TDList tdList){
        TDList buflist=listRepository.findByIdEquals(tdList.getId());
        buflist.setComplete(tdList.getComplete());
        listRepository.deleteById(tdList.getId());
        listRepository.save(buflist);
        return "redirect:/auth/index";
    }

    @GetMapping("/listOfUsers")
    public String checkRole(Principal principal,Model model){
        users.clear();
        users=userRepository.findAll();
        User user=userRepository.findByEmail(principal.getName());
        Role userRole=USER;
        Role adminRole=ADMIN;
        Status activeStatus=ACTIVE;
        Status bannedStatus=BANNED;
        model.addAttribute("user",user);
        model.addAttribute("users",users);
        model.addAttribute("userRole",userRole);
        model.addAttribute("adminRole",adminRole);
        return "listOfUsers";
    }
    @PostMapping("/changeRole")
    private String changeRole(User user){
        User bufuser=userRepository.findByEmail(user.getEmail());
        if(user.getRole()==USER){
            bufuser.setRole(ADMIN);
        }else{
            bufuser.setRole(USER);
        }
        userRepository.deleteById(user.getId());
        userRepository.save(bufuser);
        return "redirect:/auth/listOfUsers";
    }

    @PostMapping("/changeStatus")
    private String changeStatus(User user){
        User bufuser=userRepository.findByEmail(user.getEmail());
        if(user.getStatus()==ACTIVE){
            bufuser.setStatus(BANNED);
        }else{
            bufuser.setStatus(ACTIVE);
        }
        userRepository.deleteById(user.getId());
        userRepository.save(bufuser);
        return "redirect:/auth/listOfUsers";
    }

}
