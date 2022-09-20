package com.example.crudAPICheckpoint;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {

    UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public Iterable<User> getAll() {
        return this.repository.findAll();
    }

    @PostMapping()
    public User addUser(@RequestBody Map<String, String> body) {
        User newUser = new User();
        newUser.setEmail(body.get("email"));
        newUser.setPassword(body.get("password"));
        System.out.println("==========="+newUser.getEmail());
        return this.repository.save(newUser);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {

        return this.repository.findById(id).orElseThrow(() ->
                new NoSuchElementException((String.format("Record with id %d not present", id))));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchElementException.class})
    public String HandleNoSuchElementException(Exception e) {
        return e.getMessage();
    }

    @PatchMapping("/{id}")
    public User updateUserById(@PathVariable long id, @RequestBody Map<String, String> body ) {
        User userDTO = this.repository.findById(id).orElseThrow(() ->
                new NoSuchElementException((String.format("Record with id %d not present", id))));

        body.forEach((key, value) -> {
            if (key.equals("email")) {
                userDTO.setEmail(value);
            }else if (key.equals("password")) {
                userDTO.setPassword(value);
            }
        });
        return this.repository.save(userDTO);
    }



}
