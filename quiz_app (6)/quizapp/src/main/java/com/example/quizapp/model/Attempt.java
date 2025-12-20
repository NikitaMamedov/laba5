package com.example.quizapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Map;

@Data
@Entity
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Quiz quiz;

    private int score;

    // questionId -> answerOptionId
    @ElementCollection
    private Map<Long, Long> answers;
}

