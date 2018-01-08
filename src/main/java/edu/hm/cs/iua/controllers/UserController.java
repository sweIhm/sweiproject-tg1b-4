package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileNotFoundException;
import edu.hm.cs.iua.exceptions.storage.StorageOperationException;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.UserProfile;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.utils.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping @ResponseBody
    public List<UserProfile> listAll() {
        final List<UserProfile> users = new ArrayList<>((int)userRepository.count());
        userRepository.findAll().forEach(user -> users.add(user.getProfile()));
        return users;
    }

    @GetMapping("{id}") @ResponseBody
    public UserProfile find(@PathVariable Long id)
            throws UserNotFoundException {

        final IUAUser user = userRepository.findOne(id);
        if (user == null || !user.isValidated())
            throw new UserNotFoundException();
        return user.getProfile();
    }

    @GetMapping(value = "{id}/picture", produces =  MediaType.IMAGE_PNG_VALUE) @ResponseBody
    public void getProfilePicture(@PathVariable Long id, HttpServletResponse response)
            throws StorageFileNotFoundException, StorageOperationException {

        final Resource file = storageService.loadAsResource("user_" + id.toString() + ".png");
        try {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setContentLengthLong(file.contentLength());
            StreamUtils.copy(file.getInputStream(), response.getOutputStream());
        } catch (IOException e) {
            throw new StorageOperationException("Failed to read file.", e);
        }
    }

    @PostMapping(value = "{id}/picture", produces =  MediaType.IMAGE_PNG_VALUE) @ResponseBody
    public void uploadProfilePicture(@PathVariable Long id, @RequestParam String token,
                                       @RequestParam("file") MultipartFile file, HttpServletResponse response)
            throws StorageException, InvalidTokenException, InvalidDataException {

        tokenRepository.verify(id, token);

        final int fileTypeStartIndex = file.getOriginalFilename().lastIndexOf('.');
        if (fileTypeStartIndex < 0)
            throw new InvalidDataException("No file type specified in: " + file.getOriginalFilename());
        final String fileType = file.getOriginalFilename().substring(fileTypeStartIndex).toUpperCase();
        if (!fileType.equals(".PNG"))
            throw new InvalidDataException("Invalid file type: " + fileType);

        storageService.store(file, "user_" + id.toString() + ".png");
        getProfilePicture(id, response);
    }

}
