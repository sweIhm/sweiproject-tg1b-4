package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}