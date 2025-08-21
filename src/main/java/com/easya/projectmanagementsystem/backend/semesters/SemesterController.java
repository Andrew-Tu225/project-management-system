package com.easya.projectmanagementsystem.backend.semesters;

import com.easya.projectmanagementsystem.backend.dto.SemesterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semester")
public class SemesterController {

    private final SemesterService semesterService;

    @Autowired
    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemesterDTO> getSemesterById(@RequestBody String username, @PathVariable Long id){
        SemesterDTO semesterDTO = semesterService.getSemesterByIdFromUser(id);
        if(semesterDTO.getUser().getUsername().equals(username)){
            return ResponseEntity.ok(semesterDTO);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<SemesterDTO>> getAllSemestersFromUser(@RequestBody String username){
        return ResponseEntity.ok(semesterService.getAllSemestersFromUser(username));
    }

    @GetMapping("/sort={ascending}")
    public ResponseEntity<List<SemesterDTO>> getSemestersFromUserSortedByDate(@RequestBody String username, @PathVariable boolean ascending){
        return ResponseEntity.ok(semesterService.getSemestersFromUserSortedByDate(username, ascending));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSemester(@RequestBody SemesterDTO semesterDTO){
        try{
            semesterService.createSemester(semesterDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<SemesterDTO> updateSemester(@RequestBody SemesterDTO semesterDTO, @PathVariable Long id){
        try{
            Semester updatedSemester = semesterService.updateSemester(id, semesterDTO);
            SemesterDTO updatedSemesterDTO = semesterService.convertToDTO(updatedSemester);
            return ResponseEntity.ok(updatedSemesterDTO);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteSemester(@RequestBody String username, @PathVariable Long id){
        return semesterService.deleteSemester(username, id);
    }


}
