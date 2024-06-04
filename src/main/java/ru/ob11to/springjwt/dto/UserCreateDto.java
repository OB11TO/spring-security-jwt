package ru.ob11to.springjwt.dto;

public record UserCreateDto(String username,
                            String rawPassword,
                            String firstname,
                            String lastname) {

}

