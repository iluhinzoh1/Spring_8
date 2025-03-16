package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private UserService userService;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping()
    public String Users(Model model, Principal principal) {
        List<User> user = userService.findAllUsers();
        model.addAttribute("allUsers", user);
        return "admin";
    }
    @GetMapping("addNewUser")
    public String saveUser(Model model) {
        model.addAttribute("save", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-info";
    }
    @PostMapping("/saveUsers")
    public String saveUser(@ModelAttribute("save") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/updateUser")
    public String updateUsers(@RequestParam(value = "username") String username, Model model) {
        model.addAttribute("updateUserId", userService.findByUserName(username));
        model.addAttribute("roles", roleRepository.findAll());
        return "update";
    }

    @PostMapping("updateUserById")
    public String updateUserById(@ModelAttribute("updateUserId") User user,
                                 @RequestParam(required = false) String newPassword, Principal principal) {
        User existingUser = userService.findByUserName(user.getUsername());
        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        existingUser.setUsername(user.getUsername());
        existingUser.setRoles(user.getRoles());
        if (newPassword != null && !newPassword.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }
        userService.updateUser(existingUser);

        if (principal.getName().equals(existingUser.getUsername())) {
            // Если да, обновляем аутентификацию
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    existingUser, existingUser.getPassword(), existingUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        if (existingUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            return "redirect:/admin"; // Если есть роль ADMIN, перенаправляем на /admin
        } else if (existingUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER"))){
            return "redirect:/user"; // Если нет роли ADMIN, перенаправляем на /user
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("deleteUser")
    public String deleteUserById(@RequestParam(value = "id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
