package org.zavrsni.backend.field;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.field.dto.AddFieldDTO;

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

}
