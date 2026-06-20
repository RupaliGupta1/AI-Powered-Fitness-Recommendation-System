package com.fitness.aiservice.service;

import com.fitness.aiservice.controller.RecommentationController;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repo.RecommentationRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommentationService {
    private final RecommentationRepository recommentationRepository;

    public List<Recommendation> getUserRecommendation(String userId) {
        return recommentationRepository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String activityId) {
        return recommentationRepository.findByActivityId(activityId).orElseThrow(()->new RuntimeException("Invalid activity ID : "+activityId));
    }
}
