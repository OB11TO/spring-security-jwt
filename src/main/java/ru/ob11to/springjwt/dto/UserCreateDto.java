package ru.ob11to.springjwt.dto;

public record UserCreateDto(Short userRoleId,
                            String login,
                            String password,
                            String name) {

}

