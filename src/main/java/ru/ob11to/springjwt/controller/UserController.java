package ru.ob11to.springjwt.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.ob11to.springjwt.dto.UserCreateDto;
import ru.ob11to.springjwt.dto.UserReadDto;
import ru.ob11to.springjwt.service.UserService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User", description = "Пользователи")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserReadDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserReadDto> findById(@PathVariable Integer id) {
        return userService.findById(id)
                .map(userReadDto -> ResponseEntity.ok().body(userReadDto))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<UserReadDto> createUser(@RequestBody @Validated UserCreateDto userCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userCreateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workplace_admin')")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer id) {
        if (!userService.deleteUser(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(id);
    }
}
