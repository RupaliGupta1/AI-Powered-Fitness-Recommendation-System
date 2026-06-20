package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {
//       boolean isValidUser= userValidationService.validateUser(request.getUserId());
//       if(!isValidUser){
//           throw new RuntimeException("Invalid user id "+request.getUserId());
//       }

        // Trust API Gateway — user is already validated
        Activity activity=Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurns(request.getCaloriesBurns())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics()).build();
        Activity savedActivity=activityRepository.save(activity);
        //publish to rabbitmq to ai processing
        try{
           rabbitTemplate.convertAndSend(exchange,routingKey,savedActivity);
        }catch (Exception e){
           log.error("Failed to publish the acitivity: "+e);
        }
        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse response=new ActivityResponse();
        response.setId(savedActivity.getId());
        response.setUserId(savedActivity.getUserId());
        response.setType(savedActivity.getType());
        response.setCaloriesBurns(savedActivity.getCaloriesBurns());
        response.setDuration(savedActivity.getDuration());
        response.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        response.setStartTime(savedActivity.getStartTime());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());

        return response;
    }

    public List<ActivityResponse> getUserActivity(String t_UserId) {
        List<Activity> activities=activityRepository.findByUserId(t_UserId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    public ActivityResponse activityId(String activityId) {
        Activity activity=activityRepository.findById(activityId)
                .orElseThrow(()->new RuntimeException("activity not present with id "+activityId));
        return mapToResponse(activity);
    }
}
