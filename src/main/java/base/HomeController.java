package base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // so framework can recognize this as a controller class
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String index() { return "index.html"; }

}