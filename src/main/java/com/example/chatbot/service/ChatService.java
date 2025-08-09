package com.example.chatbot.service;

import com.example.chatbot.dto.GeminiDto;
import com.example.chatbot.model.ChatMessage;
import com.example.chatbot.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getChatResponse(String userMessage, String username) {
        chatMessageRepository.save(new ChatMessage(username, userMessage, ChatMessage.Sender.USER));

        // Check if API key is configured
        if (apiKey == null || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            String fallbackResponse = "Hello! I'm a demo chatbot. Your message: '" + userMessage + "' has been received. Please configure your Gemini API key for AI responses.";
            chatMessageRepository.save(new ChatMessage(username, fallbackResponse, ChatMessage.Sender.AI));
            return fallbackResponse;
        }

        String requestUrl = apiUrl + "?key=" + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String systemPrompt = "You are a helpful AI assistant. Respond in a conversational, friendly, and informative manner similar to ChatGPT. Be concise but thorough, use proper formatting with line breaks for readability, and provide practical examples when helpful. Always be polite and professional.\n\nUser: " + userMessage;
        GeminiDto.GeminiRequest geminiRequest = new GeminiDto.GeminiRequest(systemPrompt);
        HttpEntity<GeminiDto.GeminiRequest> entity = new HttpEntity<>(geminiRequest, headers);

        try {
            logger.info("Calling Gemini API with URL: {}", requestUrl.substring(0, requestUrl.indexOf("?key=")));
            logger.info("Request body: {}", geminiRequest);
            GeminiDto.GeminiResponse response = restTemplate.postForObject(requestUrl, entity, GeminiDto.GeminiResponse.class);
            
            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                String aiResponseText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                chatMessageRepository.save(new ChatMessage(username, aiResponseText, ChatMessage.Sender.AI));
                return aiResponseText;
            }
            
            logger.warn("Empty response from Gemini API");
            String fallbackResponse = "I received your message but couldn't generate a proper response. Please try rephrasing your question.";
            chatMessageRepository.save(new ChatMessage(username, fallbackResponse, ChatMessage.Sender.AI));
            return fallbackResponse;
            
        } catch (Exception e) {
            logger.error("Error calling Gemini API: {}", e.getMessage());
            logger.error("Full error: ", e);
            String errorResponse = "API Error: " + e.getMessage() + ". Please check your API key and try again.";
            chatMessageRepository.save(new ChatMessage(username, errorResponse, ChatMessage.Sender.AI));
            return errorResponse;
        }
    }
}