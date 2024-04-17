package org.zavrsni.backend.sportCenter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.field.dto.FieldsMetadataDTO;
import org.zavrsni.backend.sportCenter.dto.AddSportCenterDTO;
import org.zavrsni.backend.sportCenter.dto.FilteredSportCenterDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterDetailsDTO;

import java.util.List;

@RestController
@RequestMapping("/sport-center")
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequiredArgsConstructor
public class SportCenterController {

    private final SportCenterService sportCenterService;

    @PostMapping("/add")
    public ResponseEntity<Void> addSportCenter(@ModelAttribute AddSportCenterDTO addSportCenterDTO) {
        return ResponseEntity.ok(sportCenterService.addSportCenter(addSportCenterDTO));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<SportCenterDetailsDTO>> getAllSportCentersAdmin() {
        return ResponseEntity.ok(sportCenterService.getAllSportCentersAdmin());
    }

    @GetMapping("/{sportCenterId}")
    public ResponseEntity<SportCenterDetailsDTO> getSportCenterDetails(@PathVariable Long sportCenterId) {
        return ResponseEntity.ok(sportCenterService.getSportCenterById(sportCenterId));
    }

    @PutMapping("/update/{sportCenterId}")
    public ResponseEntity<Void> updateSportCenter(@PathVariable Long sportCenterId, @ModelAttribute AddSportCenterDTO addSportCenterDTO) {
        return ResponseEntity.ok(sportCenterService.updateSportCenter(sportCenterId, addSportCenterDTO));
    }

    @PutMapping("/deactivate/{sportCenterId}")
    public ResponseEntity<Void> deactivateSportCenter(@PathVariable Long sportCenterId, @RequestBody String reason) {
        return ResponseEntity.ok(sportCenterService.deactivateSportCenter(sportCenterId, reason));
    }

    @GetMapping("/fields/{sportCenterId}")
    public ResponseEntity<List<FieldsMetadataDTO>> getSportCenterFields(@PathVariable Long sportCenterId) {
        return ResponseEntity.ok(sportCenterService.getSportCenterFields(sportCenterId));
    }

    @PostMapping("/all")
    public ResponseEntity<List<SportCenterDetailsDTO>> getAllSportCenters(@RequestBody FilteredSportCenterDTO filteredSportCenterDTO) {
        return ResponseEntity.ok(sportCenterService.getAllSportCenters(filteredSportCenterDTO));
    }
}
