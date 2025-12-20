package com.example.quizapp.repository;

import com.example.quizapp.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByQuizId(Long quizId);
    List<Attempt> findTop5ByQuizIdOrderByScoreDesc(Long quizId);
}
