package com.banghyang.object.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserRequest {

    @JsonProperty("user_input")
    private String userInput;
}
