package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.models.Activity;
import edu.hm.cs.iua.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
  
    @Autowired
    private ActivityRepository activityRepository;

    @GetMapping
    public List<Activity> listAll() {
        List<Activity> activities = new ArrayList<>();
        activityRepository.findAll().forEach(activities::add);
        return activities;
    }

    @GetMapping("{id}")
    public Activity find(@PathVariable Long id) {
        return activityRepository.findOne(id);
    }

    @PostMapping
    public Activity create(@RequestBody Activity input) {
        return activityRepository.save(new Activity((long)0, input.getText(), input.getTags(), input.getTitle()));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        activityRepository.delete(id);
    }

    @PutMapping("{id}")
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
    }

}