package com.animalleague.april.professor.api;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalleague.april.professor.application.ProfessorService;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorCreateResponse> createProfessor(
        @Valid @RequestBody ProfessorCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.createProfessor(request));
    }

    @GetMapping
    public ProfessorListResponse listProfessors() {
        return professorService.listProfessors();
    }

    @GetMapping("/{professorId}")
    public ProfessorDetailResponse getProfessorDetail(@PathVariable UUID professorId) {
        return professorService.getProfessorDetail(professorId);
    }
}
