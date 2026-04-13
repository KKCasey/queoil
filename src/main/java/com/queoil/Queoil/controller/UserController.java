package com.queoil.Queoil.controller;

import com.queoil.Queoil.model.User;
import com.queoil.Queoil.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public User createUser() {
        User user = new User();
        user.setUsername("testmusician");
        user.setEmail("testmusician@email.com");
        user.setPassword("1234");
        user.setRole("MUSICIAN");

        return userRepository.save(user);
    }
}