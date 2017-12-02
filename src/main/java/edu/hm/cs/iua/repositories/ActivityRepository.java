package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.Activity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
}
