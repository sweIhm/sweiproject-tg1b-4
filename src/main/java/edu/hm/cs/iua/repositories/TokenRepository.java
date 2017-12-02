package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {

}