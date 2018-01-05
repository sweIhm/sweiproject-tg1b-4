package edu.hm.cs.iua.utils;

import edu.hm.cs.iua.exceptions.storage.StorageAccessException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileEmptyException;
import edu.hm.cs.iua.exceptions.storage.StorageFileNotFoundException;
import edu.hm.cs.iua.exceptions.storage.StorageInitializeException;
import edu.hm.cs.iua.exceptions.storage.StorageOperationException;
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
            if (file.isEmpty()) {
                throw new StorageFileEmptyException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageAccessException("Cannot store file with relative path outside current directory " + filename);
            }
            Files.copy(file.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageOperationException(" Failed to store file " + filename, e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) throws StorageFileNotFoundException {
        try {
            final Path file = load(filename);
            final Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
    }

}
