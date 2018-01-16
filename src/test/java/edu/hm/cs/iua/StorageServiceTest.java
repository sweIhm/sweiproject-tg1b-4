package edu.hm.cs.iua;

import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.storage.StorageAccessException;
import edu.hm.cs.iua.exceptions.storage.StorageException;
import edu.hm.cs.iua.exceptions.storage.StorageFileEmptyException;
import edu.hm.cs.iua.exceptions.storage.StorageInitializeException;
import edu.hm.cs.iua.utils.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StorageServiceTest {

    @Autowired
    private StorageService storageService;

    @Test(expected = StorageFileEmptyException.class)
    public void emptyFileTest() throws StorageException {
        storageService.store(new MockMultipartFile("Test.png", new byte[0]), "test.png");
    }

    @Test(expected = StorageAccessException.class)
    public void invalidFileNameTest() throws StorageException {
        storageService.store(new MockMultipartFile("Test.png", new byte[1024]), "../test.png");
    }

}
