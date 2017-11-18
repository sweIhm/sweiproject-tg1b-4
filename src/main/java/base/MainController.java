package base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping({"/activities-overview", "/activities_detail/{id:\\w+}", "/overview"})
    public String index() {
        return "forward:/index.html";
    }

}
