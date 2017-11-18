package base.activityOverview;

import org.springframework.web.bind.annotation.PostMapping;


/**
 * @author Christoph Rott, rott.christoph@hm.edu
 * @version 18/11/2017.
 */
public class ActivityOverviewController {
    @PostMapping("/api/overview")
    public String index() {
        return "forward:/index.html";
    }
}
