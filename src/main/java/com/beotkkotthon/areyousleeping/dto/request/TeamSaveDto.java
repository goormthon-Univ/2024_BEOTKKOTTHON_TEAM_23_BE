package com.beotkkotthon.areyousleeping.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팀 생성 DTO")
public record TeamSaveDto(
        @JsonProperty("title")
        String title,
        @JsonProperty("maxNum")
        int maxNum,
        @JsonProperty("targetTime")
        Integer targetTime,
        @JsonProperty("isSecret")
        boolean isSecret,
        @JsonProperty("password")
        String password,
        @JsonProperty("category")
        String category,
        @JsonProperty("description")
        String description
){
}
