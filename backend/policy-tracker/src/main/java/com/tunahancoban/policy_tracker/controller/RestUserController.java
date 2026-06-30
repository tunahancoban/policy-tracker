package com.tunahancoban.policy_tracker.controller;
import com.tunahancoban.policy_tracker.model.User;
import com.tunahancoban.policy_tracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest/api/user") //Root address of all paths.
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    @GetMapping(path= "/with-params")
    public List<User> getUserWithParam( @RequestParam(name = "id", required = false) String id,
                                        @RequestParam(name = "firstName", required = false) String firstName,
                                        @RequestParam(name = "lastName", required = false) String lastName,
                                        @RequestParam(name = "email", required = false) String email,
                                        @RequestParam(name = "role", required = false) String role){
        return userService.getUserWithParam(id, firstName, lastName, email, role );
    }

    @PostMapping(path="/create-user")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @DeleteMapping(path="/delete-user/{id}")
    public void deleteUser(@PathVariable(name = "id") String id){
        userService.deleteUser(id);
    }

    @PatchMapping(path = "/update-user/{id}", consumes = "application/json")
    public void updateUser(@PathVariable(name = "id") String id, @RequestBody Map<String, Object> updates){
        userService.updateUser(id, updates);
    }
}
