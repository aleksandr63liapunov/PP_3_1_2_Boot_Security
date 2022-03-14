package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/adminn")  //(ввод в браузере)
    public String findAll(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        var w=userService.findAll()
                .stream().filter(user1->user1.getEmail().equals(securityContext.getAuthentication().getName()))
                .collect(Collectors.toList()).stream().findFirst().orElse(null);
        List<User> users = userService.findAll();
        model.addAttribute("users", users);  //передать в юз html юз
        model.addAttribute("user", w);  //передать в юз html юз

        return "userList";     // путь к html файлу(название html)
    }


    
    @GetMapping("/user-create")
    public String createUserForm(User user) {
        return "userCreate";
    }

    @PostMapping("/user-create")
    public String userCreate(User user) {
        user.setRoles(Collections.singleton(new Role(1l, "ROLE_USER")));
        userService.saveUser(user);
        return "redirect:/adminn";
    }

    @GetMapping("user-delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/adminn";
    }

    @GetMapping("user-update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "userUpdate";
    }

    @PostMapping("/user-update")
    public String userUpdate(User user) {
        userService.saveUser(user);
        return "redirect:/adminn";
    }
}
