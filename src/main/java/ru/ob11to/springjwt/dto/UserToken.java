package ru.ob11to.springjwt.dto;

import java.io.Serializable;

public record UserToken(String accessToken, String refreshToken) implements Serializable {
}
