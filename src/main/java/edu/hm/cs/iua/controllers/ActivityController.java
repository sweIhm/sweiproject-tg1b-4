package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.activity.ActivityNotFoundException;
import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.InvalidUserException;
import edu.hm.cs.iua.exceptions.auth.UnauthorizedException;
import edu.hm.cs.iua.models.Activity;
import edu.hm.cs.iua.repositories.ActivityRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
  
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping
    public List<Activity> listAll() {
        final List<Activity> activities = new ArrayList<>((int)activityRepository.count());
        activityRepository.findAll().forEach(activities::add);
        return activities;
    }

    @GetMapping("{id}")
    public Activity find(@PathVariable Long id)
            throws ActivityNotFoundException {

        activityRepository.verify(id);

        return activityRepository.findOne(id);
    }

    @PostMapping
    public Activity create(@RequestParam Long user, @RequestParam String token,
                           @RequestBody Activity input)
            throws InvalidUserException, InvalidTokenException {

        tokenRepository.verify(user, token);

        return activityRepository.save(new Activity(user, input.getTitle(), input.getText(), input.getTags()));
    }

    @DeleteMapping("{id}")
    public void delete(@RequestParam Long user, @RequestParam String token,
                       @PathVariable Long id)
            throws InvalidTokenException,
            ActivityNotFoundException, UnauthorizedException {

        tokenRepository.verify(user, token);
        activityRepository.verify(id, user);

        activityRepository.delete(id);
    }

    @PutMapping("{id}")
    public void update(@RequestParam Long user, @RequestParam String token,
                           @PathVariable Long id, @RequestBody Activity input)
            throws InvalidTokenException,
            ActivityNotFoundException, UnauthorizedException {

        tokenRepository.verify(user, token);
        activityRepository.verify(id, user);

        final Activity activity = activityRepository.findOne(id);
        activity.setText(input.getText());
        activity.setTags(input.getTags());
        activity.setTitle(input.getTitle());
        activityRepository.save(activity);
    }

}