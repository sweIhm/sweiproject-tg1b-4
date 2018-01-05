package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.InvalidUserException;
import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileNotFoundException;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.UserProfile;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.utils.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("{id}/picture") @ResponseBody
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long id)
            throws StorageFileNotFoundException {

        final Resource file = storageService.loadAsResource("user_" + id.toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("{id}/picture") @ResponseBody
    public ResponseEntity<Resource> uploadProfilePicture(@PathVariable Long id, @RequestParam String token,
                                       @RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes)
            throws StorageException, InvalidTokenException, InvalidUserException {

        tokenRepository.verify(id, token);
        storageService.store(file, "user_" + id.toString());
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return getProfilePicture(id);
    }

}
