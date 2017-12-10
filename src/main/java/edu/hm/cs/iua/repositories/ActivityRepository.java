package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.exceptions.activity.ActivityNotFoundException;
import edu.hm.cs.iua.exceptions.auth.UnauthorizedException;
import edu.hm.cs.iua.models.Activity;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<Activity, Long> {

    default void verify(Long id, Long author)
            throws ActivityNotFoundException, UnauthorizedException {

        if (!this.exists(id))
            throw new ActivityNotFoundException();
        if (!this.findOne(id).getAuthor().equals(author))
            throw new UnauthorizedException();
    }

    default void verify(Long id)
            throws ActivityNotFoundException {

        if (!this.exists(id))
            throw new ActivityNotFoundException();
    }

}
