package com.yoshida.orgflow.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String login_id,
    @NotBlank String password) {

}
