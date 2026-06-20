package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;


    public Recommendation generateRecommendation(Activity activity){
        String prompt=createPromptForActivity(activity);
        String aiResponse=geminiService.getAnswer(prompt);
        log.info("Response from ai "+aiResponse);
       Recommendation recommendation=processAiResponse(activity,aiResponse);
        return recommendation;
    }

    public Recommendation processAiResponse(Activity activity,String activityResponse){
    try{
        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode jsonNode=objectMapper.readTree(activityResponse);

        JsonNode textNode = jsonNode
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text");
        String jsonContent=textNode.asText()
                .replaceAll("```json\\n"," ")
                .replaceAll("\\n```"," ")
                .trim();
        log.info("Parsed response from AI "+jsonContent);

        JsonNode analysisJson= objectMapper.readTree(jsonContent);
        JsonNode analysisNode=analysisJson.path("analysis");

        StringBuilder fullAnalysis=new StringBuilder();
        addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall:");
        addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace:");
        addAnalysisSection(fullAnalysis,analysisNode,"heartRate","HeartRate:");
        addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","CaloriesBurned:");

        List<String> improvements=extractImprovement(analysisJson.path("improvements"));
        List<String> suggestions=extractSuggestions(analysisJson.path("Suggestions"));
        List<String> safety=extractSafety(analysisJson.path("safety"));

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType().name())
                .recommendations(fullAnalysis.toString().trim())
                .improvement(improvements)
                .suggestions(suggestions)
                .safety(safety)
                .createdAt(LocalDateTime.now()).build();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return generateDefaultRecommendation(activity);
    }

    private Recommendation generateDefaultRecommendation(Activity activity) {
        String recommendations = "Keep maintaining a consistent routine.";
        String improvement = "Try to gradually increase intensity or duration.";
        String suggestions = "Stay hydrated and track your progress regularly.";
        String safety = "Avoid overexertion and listen to your body.";

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType().name())
                .recommendations(recommendations)
                .improvement(Collections.singletonList(improvement))
                .suggestions(Collections.singletonList(suggestions))
                .safety(Collections.singletonList(safety))
                .createdAt(LocalDateTime.now()).build();

    }

    private List<String> extractSafety(JsonNode safetyNode) {
        List<String> safety=new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item->safety.add(item.asText()));
        }
        return safety.isEmpty()? Collections.singletonList("Follow general safety guidelines")
                :safety;

    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions=new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion->{
                String workout=suggestion.path("workout").asText();
                String description=suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s",workout,description));
            });
        }
        return suggestions.isEmpty()? Collections.singletonList("No specific suggestions provided")
                :suggestions;

    }

    private List<String> extractImprovement(JsonNode improvementNode) {
        List<String> improvements=new ArrayList<>();
        if(improvementNode.isArray()){
            improvementNode.forEach(improvement->{
                String area=improvement.path("area").asText();
                String details=improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s",area,details));
            });
        }
        return improvements.isEmpty()? Collections.singletonList("No specific improvemnt provided")
                :improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    //create prompt for activity
    private String createPromptForActivity(Activity activity) {
        return String.format("""
      Analysis the fitness activity and provide recommenrdation according to the parameter
{
  "analysis": {
    "overall": "High-intensity short workout with strong calorie burn.",
    "pace": "Fast and consistent pace maintained.",
    "heartRate": "High heart rate indicating intense effort.",
    "caloriesBurned": "Efficient calorie burn in short duration."
  },
  "improvements": [
    {
      "area": "endurance",
      "recommendation": "Increase workout duration"    },
    {
      "area": "fat_loss",
      "recommendation": "Add interval training"    },
    {
      "area": "recovery",
      "recommendation": "Include cool-down and stretching",    }
  ],
  "Suggestions":[
  "workout":"Internal training run",
  "description":"Take 15min run and more add"
  ]//like this can add more suggestions as well
  ,"safety":[
  add list of safety to be taken
  ]
}
  ,Analysis the activity
    Activity Type: %s
    Duration: %d minutes
    Calories burned: %d
    Additional metrics: %s
    
    Provide detailed analysis focusing on performance,imprvement and safety ,
     give the response in the similar json format shown above 
                """,activity.getType(),activity.getDuration(),activity.getCaloriesBurns(),activity.getAdditionalMetrics());
    }

}
