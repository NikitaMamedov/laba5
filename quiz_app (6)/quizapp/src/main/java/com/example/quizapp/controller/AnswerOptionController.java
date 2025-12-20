package com.example.quizapp.controller;

import com.example.quizapp.model.AnswerOption;
import com.example.quizapp.repository.AnswerOptionRepository;
import com.example.quizapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class AnswerOptionController {

    private final AnswerOptionRepository optionRepo;
    private final QuestionRepository questionRepo;

    @PostMapping
    public AnswerOption create(@Valid @RequestBody AnswerOption option) {
        var question = questionRepo.findById(option.getQuestion().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        if (question.getQuiz().isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is locked");
        }

        return optionRepo.save(option);
    }

    @GetMapping
    public List<AnswerOption> getAll() {
        return optionRepo.findAll();
    }

    @GetMapping("/{id}")
    public AnswerOption getById(@PathVariable Long id) {
        return optionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Option not found"));
    }

    @PutMapping("/{id}")
    public AnswerOption update(@PathVariable Long id, @Valid @RequestBody AnswerOption updated) {
        var existing = optionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existing.getQuestion().getQuiz().isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is locked");
        }

        // Обновляем только разрешённые поля
        existing.setText(updated.getText());
        existing.setCorrect(updated.isCorrect());

        return optionRepo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        var existing = optionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (existing.getQuestion().getQuiz().isLocked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete: quiz is locked");
        }

        optionRepo.delete(existing);
    }
}
