package org.example;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * POJO representing a weather report with validation constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReport {
    @NotBlank
    private String city;

    @JsonProperty("temperature_c")
    @DecimalMin("-100.0")
    @DecimalMax("100.0")
    private Double temperatureC;

    @JsonProperty("feels_like_c")
    @DecimalMin("-100.0")
    @DecimalMax("100.0")
    private Double feelsLikeC;

    @NotNull
    @Size(max = 10)
    private List<@NotBlank String> alerts;
}

