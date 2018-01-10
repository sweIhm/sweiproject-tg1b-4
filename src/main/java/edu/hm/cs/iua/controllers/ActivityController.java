package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.activity.ActivityNotFoundException;
import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.UnauthorizedException;
import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileNotFoundException;
import edu.hm.cs.iua.models.Activity;
import edu.hm.cs.iua.repositories.ActivityRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.utils.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private StorageService storageService;
  
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
            throws InvalidTokenException {

        tokenRepository.verify(user, token);
        final Activity activity = new Activity(input.getDay(), input.getMonth(), input.getYear(), user, input.getTitle(), input.getText(), input.getTags());
        activity.setCapacity(input.getCapacity());
        return activityRepository.save(activity);
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
        activity.setDay(input.getDay());
        activity.setMonth(input.getMonth());
        activity.setYear(input.getYear());
        activity.setCapacity(input.getCapacity());
        activityRepository.save(activity);
    }

    @GetMapping("{id}/picture")
    public ResponseEntity<Resource> getActivityPicture(@PathVariable Long id)
            throws StorageFileNotFoundException {

        final Resource file = storageService.loadAsResource("activity_" + id.toString() + ".png");
        return ResponseEntity.ok().header("Content-Type", "image/png").body(file);
    }

    @PostMapping(value = "{id}/picture", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> uploadActivityPicture(@PathVariable Long id, @RequestParam Long user, @RequestParam String token,
                                      @RequestParam("file") MultipartFile file)
            throws InvalidTokenException, ActivityNotFoundException,
            UnauthorizedException, InvalidDataException, StorageException {

        tokenRepository.verify(user, token);
        activityRepository.verify(id, user);

        storageService.verify(file);

        storageService.store(file, "activity_" + id.toString() + ".png");
        return getActivityPicture(id);
    }

}