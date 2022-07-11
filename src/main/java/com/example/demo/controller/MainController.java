package com.example.demo.controller;

import com.example.demo.model.TDList;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.ListRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {

    private final UserRepository userRepository;

    @Autowired
    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    @Resource
    private ListRepository listRepository;

    @Autowired
    @Resource
    private TaskRepository taskRepository;

    private List<Task> tasks = new ArrayList<>();

    private List<TDList> lists = new ArrayList<>();

    private String email;

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

    @GetMapping("/addTask")
    public String getList(Model model) {
        tasks.clear();
//        listId=tdlist.getId().toString();
//        model.addAttribute("tdList",tdlist);
        tasks.addAll(taskRepository.findAll());
        model.addAttribute("tasks", tasks);
        return "addTask";
    }

    @PostMapping("/addList")
    public String addList(TDList list, Map<String, Object> model) {
        list.setUserEmail(email);
        listRepository.save(list);
        return "redirect:/index";
    }

    @PostMapping("/addTask")
    public String addTask(Task task, Map<String, Object> model) {
        task.setListId("1");
        taskRepository.save(task);
        return "redirect:/addTask";
    }

    @PostMapping("/complete_check")
    public String checkComplete(TDList tdList) {
        TDList buflist = listRepository.findByIdEquals(tdList.getId());
        buflist.setComplete(tdList.getComplete());
        listRepository.deleteById(tdList.getId());
        listRepository.save(buflist);
        return "redirect:/index";
    }
}
