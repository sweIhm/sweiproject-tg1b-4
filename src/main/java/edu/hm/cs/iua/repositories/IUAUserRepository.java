package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.IUAUser;
import org.springframework.data.repository.CrudRepository;

public interface IUAUserRepository extends CrudRepository<IUAUser, Long> {

    default IUAUser find(String username) {
        for(IUAUser user: this.findAll()) {
            if (user.getName().equals(username))
                return user;
        }
        return null;
    }

}