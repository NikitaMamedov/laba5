package com.example.quizapp.controller;

import com.example.quizapp.model.Quiz;
import com.example.quizapp.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizRepository repo;

    @PostMapping
    public Quiz create(@Valid @RequestBody Quiz quiz) {
        return repo.save(quiz);
    }

    @GetMapping
    public List<Quiz> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Quiz getById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
    }

    @PutMapping("/{id}")
    public Quiz update(@PathVariable Long id, @Valid @RequestBody Quiz updated) {
        var existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existing.isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is locked after first attempt");
        }

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        var existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existing.isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete locked quiz");
        }

        repo.delete(existing);
    }
}


