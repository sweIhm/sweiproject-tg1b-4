package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.activity.ActivityNotFoundException;
import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.UnauthorizedException;
import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    public static final String PICTURE_NAME_PREFIX = "activity_";
    public static final String PICTURE_FILE_TYPE = ".png";
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
            throws InvalidTokenException, InvalidDataException {

        tokenRepository.verify(user, token);
        verifyActivity(input);

        final Activity activity = new Activity(getSafeDate(input.getDueDate()), user, input.getTitle(), input.getText(), input.getTags());
        if (input.getCapacity() != null)
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
        storageService.delete(PICTURE_NAME_PREFIX + id + PICTURE_FILE_TYPE);
    }

    @PutMapping("{id}")
    public void update(@RequestParam Long user, @RequestParam String token,
                           @PathVariable Long id, @RequestBody Activity input)
            throws InvalidTokenException,
            ActivityNotFoundException, UnauthorizedException, InvalidDataException {

        tokenRepository.verify(user, token);
        activityRepository.verify(id, user);
        verifyActivity(input);

        final Activity activity = activityRepository.findOne(id);
        activity.setTitle(input.getTitle());
        activity.setText(input.getText());
        activity.setTags(input.getTags());
        activity.setDueDate(getSafeDate(input.getDueDate()));
        activity.setCapacity(input.getCapacity());
        activityRepository.save(activity);
    }

    @GetMapping("{id}/picture")
    public ResponseEntity<Resource> getActivityPicture(@PathVariable Long id) {

        final Resource file = storageService.loadAsResource(PICTURE_NAME_PREFIX + id.toString() + ".png");
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

        storageService.store(file, PICTURE_NAME_PREFIX + id.toString() + ".png");
        return getActivityPicture(id);
    }

    private void verifyActivity(Activity activity) throws InvalidDataException {
        if (activity.getTitle() == null || activity.getTitle().isEmpty())
            throw new InvalidDataException("Activity must have a title!");
        if (activity.getText() == null || activity.getText().isEmpty())
            throw new InvalidDataException("Activity must have a text body!");
        if (activity.getDueDate() == null || activity.getDueDate().isEmpty())
            throw new InvalidDataException("Activity must have a valid date");
    }

    private String getSafeDate(String unsafe) throws InvalidDataException {
        if (unsafe.contains("Z"))
            unsafe = unsafe.substring(0, unsafe.indexOf('Z'));
        try {
            return LocalDateTime.parse(unsafe).toString();
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Activity must have a valid date");
        }
    }

}