package org.zavrsni.backend.sport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.sport.dto.SportDTO;
import org.zavrsni.backend.sport.dto.SportDetailsDTO;

import java.util.List;

@RestController
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequestMapping("/sport")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping("/all")
    public ResponseEntity<List<SportDTO>> getAllSports() {
        return ResponseEntity.ok(sportService.getAllSports());
    }
    
    @GetMapping("/admin/all")
    public ResponseEntity<List<SportDetailsDTO>> getAllSportsAdmin() {
        return ResponseEntity.ok(sportService.getAdminSports());
    }

    @PostMapping("/admin/add")
    public ResponseEntity<Void> addSport(@RequestBody String sportName) {
        sportService.createSport(sportName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/delete/{sportName}")
    public ResponseEntity<Void> deleteSport(@PathVariable String sportName) {
        sportService.deleteSport(sportName);
        return ResponseEntity.ok().build();
    }
}
