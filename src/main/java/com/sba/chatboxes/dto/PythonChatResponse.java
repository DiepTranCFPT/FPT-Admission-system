package com.sba.chatboxes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PythonChatResponse {
    private String response;
    @JsonProperty("context_used")
    private List<String> contextUsed;
}
