package ru.ob11to.springjwt.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ob11to.springjwt.dto.LoginRequest;
import ru.ob11to.springjwt.dto.LoginResponse;
import ru.ob11to.springjwt.dto.UserToken;
import ru.ob11to.springjwt.entity.User;
import ru.ob11to.springjwt.exception.AuthException;
import ru.ob11to.springjwt.repository.UserRepository;
import ru.ob11to.springjwt.service.jwt.JwtProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        String login = "user";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        User user = new User();
        user.setLogin(login);
        user.setPassword(encodedPassword);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtProvider.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(user)).thenReturn(refreshToken);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        verify(tokenService, times(1)).saveUserToken(login, new UserToken(accessToken, refreshToken));
    }

    @Test
    public void testLoginUserNotFound() {
        String login = "user";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.login(loginRequest));
    }

    @Test
    public void testLoginIncorrectPassword() {
        String login = "user";
        String password = "password";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setLogin(login);
        user.setPassword(encodedPassword);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.login(loginRequest));
    }

    @Test
    public void testGetAccessTokenSuccess() {
        String refreshToken = "validRefreshToken";
        String newAccessToken = "newAccessToken";
        String login = "user";

        User user = new User();
        user.setLogin(login);

        UserToken userToken = new UserToken("oldAccessToken", refreshToken);

        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(login);

        when(jwtProvider.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(tokenService.getUserToken(login)).thenReturn(userToken);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(user)).thenReturn(newAccessToken);

        LoginResponse response = authService.getAccessToken(refreshToken);

        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());
        assertNull(response.getRefreshToken());
    }

    @Test
    public void testGetAccessTokenInvalidToken() {
        String refreshToken = "invalidRefreshToken";

        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(false);

        LoginResponse response = authService.getAccessToken(refreshToken);

        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
    }

    @Test
    public void testGetAccessTokenUserNotFound() {
        String refreshToken = "validRefreshToken";
        String login = "user";

        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(login);

        when(jwtProvider.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(tokenService.getUserToken(login)).thenReturn(null);

        LoginResponse response = authService.getAccessToken(refreshToken);

        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
    }
}
