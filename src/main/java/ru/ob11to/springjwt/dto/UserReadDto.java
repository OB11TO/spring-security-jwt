package ru.ob11to.springjwt.dto;

public record UserReadDto(Integer id,
                          String userRoleName,
                          String login,
                          String name) {

}
