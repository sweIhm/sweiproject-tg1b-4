package edu.hm.cs.iua.exceptions.activity;

import edu.hm.cs.iua.exceptions.IUAException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ActivityException extends IUAException {

    public ActivityException() {
        super();
    }

    public ActivityException(String message) {
        super(message);
    }

}
