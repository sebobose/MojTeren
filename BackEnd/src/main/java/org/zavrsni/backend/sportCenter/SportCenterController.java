package org.zavrsni.backend.sportCenter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.sportCenter.dto.AddSportCenterDTO;

@RestController
@RequestMapping("/sport-center")
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequiredArgsConstructor
public class SportCenterController {

    private final SportCenterService sportCenterService;

    @PostMapping("/admin/add")
    public ResponseEntity<Void> addSportCenter(@ModelAttribute AddSportCenterDTO addSportCenterDTO) {
        return ResponseEntity.ok(sportCenterService.addSportCenter(addSportCenterDTO));
    }
}
