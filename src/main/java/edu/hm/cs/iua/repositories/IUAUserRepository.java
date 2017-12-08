package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.IUAUser;
import org.springframework.data.repository.CrudRepository;

public interface IUAUserRepository extends CrudRepository<IUAUser, Long> {

}