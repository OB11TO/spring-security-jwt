package ru.ob11to.springjwt.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.ob11to.springjwt.dto.UserToken;
import ru.ob11to.springjwt.service.jwt.JwtProvider;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class BlacklistTokenService {

    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    public boolean isTokenExistInBlacklist(String token) {
        log.info("Check if token exists in blacklist: {}", token);
        if (token == null) {
            log.info("Token is NOT blacklisted");
            return false;
        }
        var claims = jwtProvider.getAccessClaims(token);
        var login = claims.getSubject();

        boolean isExists = tokenService.getTokenBlackList(login)
                .map(UserToken::accessToken)
                .filter(accessToken -> accessToken.equals(token))
                .isPresent();

        log.info("Token is {} blacklisted", isExists ? "" : "NOT");
        return isExists;
    }

    public void saveTokenBlackList(String bearer) {
        String token = bearer;
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            token = bearer.substring(7);
        }
        var claims = jwtProvider.getAccessClaims(token);
        var login = claims.getSubject();
        tokenService.saveTokenBlackList(login, new UserToken(token, null));
    }
}
