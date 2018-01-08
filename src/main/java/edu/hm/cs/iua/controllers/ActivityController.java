package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.activity.ActivityNotFoundException;
import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.InvalidUserException;
import edu.hm.cs.iua.exceptions.auth.UnauthorizedException;
import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileNotFoundException;
import edu.hm.cs.iua.exceptions.storage.StorageOperationException;
import edu.hm.cs.iua.models.Activity;
import edu.hm.cs.iua.repositories.ActivityRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.utils.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping(value = "{id}/picture", produces =  MediaType.IMAGE_PNG_VALUE)
    public void getProfilePicture(@PathVariable Long id, HttpServletResponse response)
            throws StorageFileNotFoundException, StorageOperationException {

        final Resource file = storageService.loadAsResource("activity_" + id.toString() + ".png");
        try {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setContentLengthLong(file.contentLength());
            StreamUtils.copy(file.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            throw new StorageOperationException("Failed to read file.", e);
        }
    }

    @PostMapping(value = "{id}/picture", produces = MediaType.IMAGE_PNG_VALUE)
    public void uploadActivityPicture(@PathVariable Long id, @RequestParam Long user, @RequestParam String token,
                                      @RequestParam("file") MultipartFile file, HttpServletResponse response)
            throws InvalidTokenException, ActivityNotFoundException,
            UnauthorizedException, InvalidDataException, StorageException {

        tokenRepository.verify(user, token);
        activityRepository.verify(id, user);

        final int fileTypeStartIndex = file.getOriginalFilename().lastIndexOf('.');
        if (fileTypeStartIndex < 0)
            throw new InvalidDataException("No file type specified in: " + file.getOriginalFilename());
        final String fileType = file.getOriginalFilename().substring(fileTypeStartIndex).toUpperCase();
        if (!fileType.equals(".PNG"))
            throw new InvalidDataException("Invalid file type: " + fileType);

        storageService.store(file, "activity_" + id.toString() + ".png");
        getProfilePicture(id, response);
    }

}