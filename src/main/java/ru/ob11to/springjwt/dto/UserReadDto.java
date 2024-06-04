package ru.ob11to.springjwt.dto;

public record UserReadDto(Long id,
                          String username,
                          String password,
                          String firstname,
                          String lastname) {

}
