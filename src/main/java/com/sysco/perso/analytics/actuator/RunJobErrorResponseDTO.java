package com.sysco.perso.analytics.actuator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunJobErrorResponseDTO {
    @JsonProperty(value = "message")
    private String message;
}
