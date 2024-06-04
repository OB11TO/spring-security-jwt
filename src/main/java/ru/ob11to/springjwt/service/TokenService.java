package ru.ob11to.springjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.ob11to.springjwt.dto.UserToken;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, UserToken> redisTemplate;

    public void saveTokenBlackList(String login, UserToken userToken) {
        redisTemplate.opsForValue().set(login, userToken);
    }

    public Optional<UserToken> getTokenBlackList(String login) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(login));
    }

    public void saveUserToken(String userId, UserToken userToken) {
        redisTemplate.opsForHash().put("user_tokens", userId, userToken);
    }

    public UserToken getUserToken(String userId) {
        return (UserToken) redisTemplate.opsForHash().get("user_tokens", userId);
    }
}
