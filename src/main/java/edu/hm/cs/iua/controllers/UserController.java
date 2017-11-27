package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.models.User;
import edu.hm.cs.iua.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public ArrayList<User> listAll() {
        ArrayList<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @GetMapping("{email, password}")
    public User find(@PathVariable String email, String password) {
        ArrayList<User> users = listAll();
        Long userID = null;
        for (User user: users) {
            if(user.getUserEmail().equals(email) && user.getUserPassword().equals(password)) {
                userID = user.getUserID();
                break;
            }
        }
        if (userID == null) {
            //throw new UserNotInTheSystemException();
        }
        return userRepository.findOne(userID);
    }

    @PostMapping
    public User create(@RequestBody User input) {
        return userRepository.save(new User(input.getUserName(), input.getUserEmail(), input.getUserPassword()));
    }

    /*@DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        activityRepository.delete(id);
    }*/

    /*@PutMapping("{id}")
    public Activity update(@PathVariable Long id, @RequestBody Activity input) {
        Activity activity = activityRepository.findOne(id);
        if (activity == null) {
            return null;
        } else {
            activity.setText(input.getText());
            activity.setTags(input.getTags());
            activity.setTitle(input.getTitle());
            return activityRepository.save(activity);
        }
    }*/
}
