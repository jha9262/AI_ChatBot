package com.example.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.List;

public class GeminiDto {

    @Data
    @NoArgsConstructor
    public static class GeminiRequest {
        private List<Content> contents;

        public GeminiRequest(String text) {
            this.contents = Collections.singletonList(new Content(Collections.singletonList(new Part(text))));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @NoArgsConstructor
    public static class GeminiResponse {
        @JsonProperty("candidates")
        private List<Candidate> candidates;
    }

    @Data
    @NoArgsConstructor
    public static class Candidate {
        @JsonProperty("content")
        private Content content;
    }
}