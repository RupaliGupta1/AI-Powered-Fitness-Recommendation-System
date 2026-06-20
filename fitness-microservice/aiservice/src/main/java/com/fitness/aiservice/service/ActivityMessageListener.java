package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repo.RecommentationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;

    private final RecommentationRepository recommentationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        try {
            log.info("Processing activity {}", activity.getId());

            Recommendation recommendation =
                    activityAIService.generateRecommendation(activity);

            if (recommendation != null) {
                recommentationRepository.save(recommendation);
            }

        } catch (Exception e) {
            log.error("Error processing activity {}", activity.getId(), e);

            // ❌ DO NOT rethrow
            // Otherwise infinite retry loop
        }
    }

}
