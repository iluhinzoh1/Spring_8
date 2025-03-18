package ru.kata.spring.boot_security.demo.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

@Component
public class testUsers implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public testUsers(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleService.getAllRoles().isEmpty()) {
            Role role = new Role("ROLE_ADMIN");

        }
    }
}
