package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.UnauthorizedException;
import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.UserProfile;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.utils.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping
    public List<UserProfile> listAll() {
        final List<UserProfile> users = new ArrayList<>((int)userRepository.count());
        userRepository.findAll().forEach(user -> users.add(user.getProfile()));
        return users;
    }

    @GetMapping("{id}")
    public UserProfile find(@PathVariable Long id)
            throws UserNotFoundException {

        final IUAUser user = userRepository.findOne(id);
        if (user == null || !user.isValidated())
            throw new UserNotFoundException();
        return user.getProfile();
    }

    @GetMapping("{id}/picture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long id) {

        final Resource file = storageService.loadAsResource("user_" + id.toString() + ".png");
        return ResponseEntity.ok().header("Content-Type", "image/png").body(file);
    }

    @PostMapping("{id}/picture")
    public ResponseEntity<Resource> uploadProfilePicture(@PathVariable Long id, @RequestParam Long user, @RequestParam String token,
                                       @RequestParam("file") MultipartFile file)
            throws StorageException, InvalidTokenException, InvalidDataException, UnauthorizedException {

        if (!user.equals(id))
            throw new UnauthorizedException();
        tokenRepository.verify(user, token);

        storageService.verify(file);

        storageService.store(file, "user_" + id.toString() + ".png");
        return getProfilePicture(id);
    }

}
