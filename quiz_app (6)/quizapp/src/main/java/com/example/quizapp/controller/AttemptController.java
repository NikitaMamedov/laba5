
package com.example.quizapp.controller;

import com.example.quizapp.model.Attempt;
import com.example.quizapp.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptRepository repo;

    @GetMapping
    public List<Attempt> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Attempt getById(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));
    }

    // CRUD запрещён — попытки создаются/завершаются только через /play
}

