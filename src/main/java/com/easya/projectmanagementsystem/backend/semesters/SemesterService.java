package com.easya.projectmanagementsystem.backend.semesters;

import com.easya.projectmanagementsystem.backend.User.UserRepository;
import com.easya.projectmanagementsystem.backend.dto.SemesterDTO;
import com.easya.projectmanagementsystem.backend.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {
    private final SemesterRepository semesterRepo;
    private final UserRepository userRepo;

    @Autowired
    public SemesterService(SemesterRepository semesterRepo, UserRepository userRepo) {
        this.semesterRepo = semesterRepo;
        this.userRepo = userRepo;
    }

    public SemesterDTO convertToDTO(Semester semester) {
        SemesterDTO semesterDTO = new SemesterDTO();
        semesterDTO.setId(semester.getId());
        semesterDTO.setName(semester.getName());
        semesterDTO.setStartDate(semester.getStartDate());
        semesterDTO.setEndDate(semester.getEndDate());
        semesterDTO.setUser(new UserDTO(semester.getUser().getUsername()));
        return semesterDTO;
    }

    public SemesterDTO getSemesterByIdFromUser(long id){
        Semester semester = semesterRepo.findById(id).orElse(null);
        if(semester == null){
            return null;
        }
        return convertToDTO(semester);
    }

    public List<SemesterDTO> getAllSemestersFromUser(String username) {
        List<Semester> semesterList = semesterRepo.findByUserUsername(username);
        List<SemesterDTO> semesterDTOList = new ArrayList<>();
        for(Semester semester : semesterList) {
            semesterDTOList.add(convertToDTO(semester));
        }
        return semesterDTOList;
    }

    public List<SemesterDTO> getSemestersFromUserSortedByDate(String username, boolean ascending) {
        List<Semester> sortedSemesterList;
        if (getAllSemestersFromUser(username).isEmpty()){
            return null;
        }
        else{
            if(ascending){
                sortedSemesterList = semesterRepo.sortSemesterWithOldest(username);
            }
            else{
                sortedSemesterList = semesterRepo.sortSemesterWithLatest(username);
            }

            List<SemesterDTO> semesterDTOList = new ArrayList<>();
            for(Semester semester : sortedSemesterList) {
                semesterDTOList.add(convertToDTO(semester));
            }
            return semesterDTOList;
        }
    }

    public void createSemester(SemesterDTO semesterDTO) throws Exception {
        Semester newSemester = new Semester();
        newSemester.setName(semesterDTO.getName());
        newSemester.setStartDate(semesterDTO.getStartDate());
        newSemester.setEndDate(semesterDTO.getEndDate());

        String username = semesterDTO.getUser().getUsername();
        if(userRepo.findByUsername(username).isPresent()){
            newSemester.setUser(userRepo.findByUsername(username).get());
        }
        else{
            throw new Exception("username is not found");
        }
        semesterRepo.save(newSemester);
    }

    public Semester updateSemester(long id, SemesterDTO semesterDTO) {
        Semester semester = semesterRepo.findById(id).orElse(null);
        semester.setName(semesterDTO.getName());
        semester.setStartDate(semesterDTO.getStartDate());
        semester.setEndDate(semesterDTO.getEndDate());
        return semesterRepo.save(semester);
    }

    public ResponseEntity<?> deleteSemester(String username, long id) {
        Optional<Semester> semester = semesterRepo.findSemesterById(id);
        if(semester.isPresent() && semester.get().getUser().getUsername().equals(username)) {
            semesterRepo.deleteById(id);
            return ResponseEntity.ok().body("semester "+semester.get().getName()+" is deleted successfully");
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
}
