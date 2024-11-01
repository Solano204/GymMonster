package infraestrucutre.Adapters.Drivens.Entities;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class controller {
    
    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello %s!", "JJ");
    }
    
}
