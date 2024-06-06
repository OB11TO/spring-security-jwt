package ru.ob11to.springjwt.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import ru.ob11to.springjwt.dto.UserCreateDto;
import ru.ob11to.springjwt.dto.UserReadDto;
import ru.ob11to.springjwt.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        List<UserReadDto> userList = List.of(new UserReadDto(1, "Admin", "jdoe", "John Doe"));
        when(userService.findAll()).thenReturn(userList);

        ResponseEntity<List<UserReadDto>> response = userController.findAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
    }

    @Test
    public void testFindByIdSuccess() {
        int userId = 1;
        UserReadDto userReadDto = new UserReadDto(userId, "Admin", "jdoe", "John Doe");
        when(userService.findById(userId)).thenReturn(Optional.of(userReadDto));

        ResponseEntity<UserReadDto> response = userController.findById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userReadDto, response.getBody());
    }

    @Test
    public void testFindByIdNotFound() {
        int userId = 1;
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userController.findById(userId));
    }

    @Test
    public void testCreateUser() {
        UserCreateDto userCreateDto = new UserCreateDto((short) 1, "jdoe", "password", "John Doe");
        UserReadDto userReadDto = new UserReadDto(1, "Admin", "jdoe", "John Doe");
        when(userService.createUser(userCreateDto)).thenReturn(userReadDto);

        ResponseEntity<UserReadDto> response = userController.createUser(userCreateDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userReadDto, response.getBody());
    }

    @Test
    public void testDeleteUserSuccess() {
        int userId = 1;
        when(userService.deleteUser(userId)).thenReturn(true);

        ResponseEntity<Integer> response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userId, response.getBody());
    }

    @Test
    public void testDeleteUserNotFound() {
        int userId = 1;
        when(userService.deleteUser(userId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userController.deleteUser(userId));
    }
}
