package com.yoshida.orgflow.dto.auth;

public record LoginResponse(
    String accessToken,
    String tokenType) {

}
