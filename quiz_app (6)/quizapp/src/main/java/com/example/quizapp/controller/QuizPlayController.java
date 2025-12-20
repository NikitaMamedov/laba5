package com.example.quizapp.controller;

import com.example.quizapp.model.Attempt;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.QuizStatistics;
import com.example.quizapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/play")
@RequiredArgsConstructor
public class QuizPlayController {

    private final QuizService quizService;

    // 1. Начать попытку (и заблокировать квиз навсегда)
    @PostMapping("/quizzes/{quizId}/users/{userId}/start")
    public Attempt startAttempt(@PathVariable Long quizId, @PathVariable Long userId) {
        return quizService.startAttempt(userId, quizId);
    }

    // 2. Завершить попытку и посчитать результат
    @PostMapping("/attempts/{attemptId}/finish")
    public Attempt finishAttempt(@PathVariable Long attemptId, @RequestBody Map<Long, Long> answers) {
        return quizService.finishAttempt(attemptId, answers);
    }

    // 3. Получить квиз для прохождения (с вопросами и вариантами)
    @GetMapping("/quizzes/{quizId}")
    public Quiz getQuizForPlay(@PathVariable Long quizId) {
        return quizService.getQuizForAttempt(quizId);
    }

    // 4. Статистика по квизу
    @GetMapping("/quizzes/{quizId}/stats")
    public QuizStatistics getStatistics(@PathVariable Long quizId) {
        return quizService.getQuizStatistics(quizId);
    }

    // 5. Таблица лидеров
    @GetMapping("/quizzes/{quizId}/leaderboard")
    public List<Attempt> getLeaderboard(@PathVariable Long quizId) {
        return quizService.getLeaderboard(quizId);
    }
}
