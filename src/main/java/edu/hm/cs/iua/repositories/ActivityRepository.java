package edu.hm.cs.iua.repositories;

import edu.hm.cs.iua.models.Activity;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
}
