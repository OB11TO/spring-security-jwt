package ru.ob11to.springjwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtToken {

    private String refreshToken;
}
