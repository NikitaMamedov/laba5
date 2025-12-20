package com.example.quizapp.controller;

import com.example.quizapp.model.Question;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepo;
    private final QuizRepository quizRepo;

    @PostMapping
    public Question create(@Valid @RequestBody Question question) {
        var quiz = quizRepo.findById(question.getQuiz().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (quiz.isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is locked");
        }

        return questionRepo.save(question);
    }

    @GetMapping
    public List<Question> getAll() {
        return questionRepo.findAll();
    }

    @GetMapping("/{id}")
    public Question getById(@PathVariable Long id) {
        return questionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable Long id, @Valid @RequestBody Question updated) {
        var existing = questionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existing.getQuiz().isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is locked");
        }

        existing.setText(updated.getText());
        return questionRepo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        var existing = questionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existing.getQuiz().isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete: quiz is locked");
        }

        questionRepo.delete(existing);
    }
}

