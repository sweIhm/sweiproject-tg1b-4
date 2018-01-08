package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.models.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {

    default void verify(Long id, String token)
            throws InvalidTokenException {

        final Token auth = this.findOne(id);
        if (auth == null || !auth.getKey().equals(token))
            throw new InvalidTokenException();
    }

}