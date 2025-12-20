package com.example.quizapp.service;

import com.example.quizapp.model.*;
import com.example.quizapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final AttemptRepository attemptRepo;
    private final QuizRepository quizRepo;
    private final UserRepository userRepo;
    private final AnswerOptionRepository optionRepo;
    private final QuestionRepository questionRepo;

    // ---------------------------------------------------------
    // 1. Начать попытку
    // ---------------------------------------------------------
    @Transactional
    public Attempt startAttempt(Long userId, Long quizId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        if (quiz.isLocked()) {
            throw new IllegalStateException("Quiz is locked — attempts are not allowed");
        }

        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setScore(0);
        attempt.setAnswers(Map.of());

        quiz.setLocked(true);                  // квиз блокируется
        quizRepo.save(quiz);

        return attemptRepo.save(attempt);
    }


    // ---------------------------------------------------------
    // 2. Завершить попытку (проверка ответов + подсчёт баллов)
    // ---------------------------------------------------------
    @Transactional
    public Attempt finishAttempt(Long attemptId, Map<Long, Long> answers) {

        Attempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found: " + attemptId));

        if (attempt.getScore() > 0) {
            throw new IllegalStateException("Attempt already finished");
        }

        int score = 0;

        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            Long optionId = entry.getValue();

            AnswerOption option = optionRepo.findById(optionId)
                    .orElseThrow(() -> new IllegalArgumentException("Answer option not found: " + optionId));

            if (!option.getQuestion().getId().equals(questionId)) {
                throw new IllegalArgumentException("Option does not belong to the question: " + optionId);
            }

            if (option.isCorrect()) {
                score++;
            }
        }

        attempt.setAnswers(answers);
        attempt.setScore(score);

        return attemptRepo.save(attempt);
    }


    // ---------------------------------------------------------
    // 3. Квиз для прохождения (вопросы + варианты)
    // ---------------------------------------------------------
    @Transactional(readOnly = true)
    public Quiz getQuizForAttempt(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        // Жадная загрузка
        quiz.getQuestions().forEach(q -> q.getOptions().size());
        return quiz;
    }


    // ---------------------------------------------------------
    // 4. Статистика (кол-во попыток, лучший результат, средний)
    // ---------------------------------------------------------
    @Transactional(readOnly = true)
    public QuizStatistics getQuizStatistics(Long quizId) {

        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        List<Attempt> attempts = attemptRepo.findByQuizId(quizId);

        if (attempts.isEmpty()) {
            return new QuizStatistics(quiz.getTitle(), 0, 0, 0.0);
        }

        int total = attempts.size();
        int best = attempts.stream().mapToInt(Attempt::getScore).max().orElse(0);
        double avg = attempts.stream().mapToInt(Attempt::getScore).average().orElse(0.0);

        return new QuizStatistics(quiz.getTitle(), total, best, avg);
    }


    // ---------------------------------------------------------
    // 5. Лидерборд (ТОП 5)
    // ---------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Attempt> getLeaderboard(Long quizId) {
        return attemptRepo.findTop5ByQuizIdOrderByScoreDesc(quizId);
    }
}

