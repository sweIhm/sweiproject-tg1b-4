package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.auth.InvalidUserException;
import edu.hm.cs.iua.models.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {

    default void verify(Long id, String token)
            throws InvalidUserException, InvalidTokenException {

        final Token auth = this.findOne(id);
        if (auth == null)
            throw new InvalidUserException();
        if (!auth.getToken().equals(token))
            throw new InvalidTokenException();
    }

}