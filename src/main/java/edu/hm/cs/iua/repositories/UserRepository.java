package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}