package edu.hm.cs.iua.utils;

import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageAccessException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileEmptyException;
import edu.hm.cs.iua.exceptions.storage.StorageInitializeException;
import edu.hm.cs.iua.exceptions.storage.StorageOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    private final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final Path rootLocation = Paths.get("data");

    public void init() throws StorageInitializeException {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageInitializeException("Could not initialize Storage", e);
        }
    }

    public void store(MultipartFile file, String filename) throws StorageException {
        try {
            if (file.isEmpty())
                throw new StorageFileEmptyException("Failed to store empty file " + filename);
            if (filename.contains(".."))
                throw new StorageAccessException("Cannot store file with relative path outside current directory " + filename);
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageOperationException(" Failed to store file " + filename, e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            final Path file = load(filename);
            final Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                if (filename.contains("user"))
                    return new ClassPathResource("user_default_picture.png");
                else
                    return new ClassPathResource("activity_default_picture.png");
        }
        catch (MalformedURLException e) {
            if (filename.contains("user"))
                return new ClassPathResource("user_default_picture.png");
            else
                return new ClassPathResource("activity_default_picture.png");
        }
    }

    public void delete(String filename) {
        final Path file = load(filename);
        try {
            Files.delete(file);
        } catch (IOException e) {
            if (file.toFile().exists())
                log.error("Could not delete file: " + filename, e);
        }
    }

    public void verify(MultipartFile file) throws InvalidDataException {
        final int fileTypeStartIndex = file.getOriginalFilename().lastIndexOf('.');
        if (fileTypeStartIndex < 0)
            throw new InvalidDataException("No file type specified in: " + file.getOriginalFilename());
        final String fileType = file.getOriginalFilename().substring(fileTypeStartIndex).toUpperCase();
        if (!fileType.equals(".PNG"))
            throw new InvalidDataException("Invalid file type: " + fileType);
    }

}
