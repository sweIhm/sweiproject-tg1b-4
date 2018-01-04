package edu.hm.cs.iua.exceptions.activity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ActivityNotFoundException extends ActivityException {

    public ActivityNotFoundException() {
        super();
    }

}
