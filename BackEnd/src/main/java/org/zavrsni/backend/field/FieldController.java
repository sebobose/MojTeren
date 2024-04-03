package org.zavrsni.backend.field;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.field.dto.AddFieldDTO;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;

@RestController
@RequestMapping("/field")
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @PostMapping("/add")
    public ResponseEntity<Void> addField(@ModelAttribute AddFieldDTO addFieldDTO) {
        return ResponseEntity.ok(fieldService.addField(addFieldDTO));
    }

    @GetMapping("/{fieldId}")
    public ResponseEntity<FieldDetailsDTO> getField(@PathVariable Long fieldId) {
        return ResponseEntity.ok(fieldService.getField(fieldId));
    }

    @PutMapping("/update/{fieldId}")
    public ResponseEntity<Void> updateField(@PathVariable Long fieldId, @ModelAttribute AddFieldDTO addFieldDTO) {
        return ResponseEntity.ok(fieldService.updateField(fieldId, addFieldDTO));
    }

    @PutMapping("/deactivate/{fieldId}")
    public ResponseEntity<Void> deactivateField(@PathVariable Long fieldId, @RequestBody String reason) {
        return ResponseEntity.ok(fieldService.deactivateField(fieldId, reason));
    }

}
