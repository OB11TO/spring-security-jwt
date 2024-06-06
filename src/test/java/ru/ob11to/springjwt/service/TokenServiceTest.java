package ru.ob11to.springjwt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;
import ru.ob11to.springjwt.dto.UserToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    @Mock
    private RedisTemplate<String, UserToken> redisTemplate;

    @Mock
    private ValueOperations<String, UserToken> valueOperations;

    @Mock
    private HashOperations<String, String, UserToken> hashOperations;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doReturn(hashOperations).when(redisTemplate).opsForHash();
    }


    @Test
    public void testSaveTokenBlackList() {
        String login = "user1";
        UserToken userToken = new UserToken("accessToken", "refreshToken");

        tokenService.saveTokenBlackList(login, userToken);

        verify(valueOperations, times(1)).set(login, userToken);
    }

    @Test
    public void testGetTokenBlackList() {
        String login = "user1";
        UserToken userToken = new UserToken("accessToken", "refreshToken");

        when(valueOperations.get(login)).thenReturn(userToken);

        Optional<UserToken> retrievedToken = tokenService.getTokenBlackList(login);

        assertTrue(retrievedToken.isPresent());
        assertEquals(userToken, retrievedToken.get());
    }

    @Test
    public void testGetTokenBlackList_notFound() {
        String login = "user1";

        when(valueOperations.get(login)).thenReturn(null);

        Optional<UserToken> retrievedToken = tokenService.getTokenBlackList(login);

        assertFalse(retrievedToken.isPresent());
    }

    @Test
    public void testSaveUserToken() {
        String userId = "user1";
        UserToken userToken = new UserToken("accessToken", "refreshToken");

        tokenService.saveUserToken(userId, userToken);

        verify(hashOperations, times(1)).put("user_tokens", userId, userToken);
    }

    @Test
    public void testGetUserToken() {
        String userId = "user1";
        UserToken userToken = new UserToken("accessToken", "refreshToken");

        when(hashOperations.get("user_tokens", userId)).thenReturn(userToken);

        UserToken retrievedToken = tokenService.getUserToken(userId);

        assertEquals(userToken, retrievedToken);
    }

    @Test
    public void testGetUserToken_notFound() {
        String userId = "user1";

        when(hashOperations.get("user_tokens", userId)).thenReturn(null);

        UserToken retrievedToken = tokenService.getUserToken(userId);

        assertNull(retrievedToken);
    }
}
