package com.example.quizapp.model;

import lombok.Data;

@Data
public class QuizStatistics {
    private String quizTitle;
    private int totalAttempts;
    private int bestScore;
    private double averageScore;

    public QuizStatistics(String quizTitle, int totalAttempts, int bestScore, double averageScore) {
        this.quizTitle = quizTitle;
        this.totalAttempts = totalAttempts;
        this.bestScore = bestScore;
        this.averageScore = Math.round(averageScore * 10.0) / 10.0;
    }
}