package com.pyokemon.did_ticketing.domain.vc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VcRequestDto {
    @NotBlank(message = "reservation ID is required")
    @Schema(description = "예약 ID", example = "1")
    private String bookingId;
}
