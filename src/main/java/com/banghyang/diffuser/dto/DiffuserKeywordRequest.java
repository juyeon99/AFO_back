package com.banghyang.diffuser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DiffuserKeywordRequest {
    @JsonProperty("language")
    private String language;

    @JsonProperty("category_index")
    private String categoryIndex;
}
