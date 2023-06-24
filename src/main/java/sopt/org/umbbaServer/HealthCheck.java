package sopt.org.umbbaServer;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthCheck {

    @GetMapping("/health")
    public ResponseEntity<Dto> healthCheck() {

        Dto dto = new Dto();
        dto.setStatus(200);

        return ResponseEntity.ok(dto);
    }

    @Data
    private class Dto {
        private int status;
    }
}
