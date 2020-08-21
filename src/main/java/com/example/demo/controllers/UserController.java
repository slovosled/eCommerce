package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            log.error("Error: User {} does not exists", username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());

        if (createUserRequest.getPassword() == null) {
            log.error("Error: User creation failed | Password for new user not provided");
            return ResponseEntity.badRequest().build();
        }

        if (createUserRequest.getConfirmPassword() == null) {
            log.error("Error: User creation failed | Password confirmation for new user not provided");
            return ResponseEntity.badRequest().build();
        }

        if (createUserRequest.getPassword().length() < 7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.error("Error: User creation failed | Cannot create user {} due to password length or password confirmation", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);

        log.info("Success: User with id: {} and name: {} successfully created",
                user.getId(),
                user.getUsername());
        return ResponseEntity.ok(user);
    }

}
