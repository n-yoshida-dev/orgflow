package com.yoshida.orgflow.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRequestInput(
    @NotBlank @Size(max = 100) String title,
    @NotNull UUID internalOrganizationId,
    @NotBlank @Pattern(regexp = "transportation_expenses|travel_expenses|equipment_purchase_expenses") String requestType,
    @NotNull @Min(0) Integer amount) {
}
