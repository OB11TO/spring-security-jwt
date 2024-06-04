package ru.ob11to.springjwt.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ob11to.springjwt.dto.UserToken;
import ru.ob11to.springjwt.entity.User;
import ru.ob11to.springjwt.exception.AuthException;
import ru.ob11to.springjwt.repository.UserRepository;
import ru.ob11to.springjwt.service.jwt.JwtProvider;
import ru.ob11to.springjwt.dto.LoginRequest;
import ru.ob11to.springjwt.dto.LoginResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginResponse login(@NonNull LoginRequest loginRequest) {
        var user = userRepository.findByLogin(loginRequest.getLogin())
                .orElseThrow(() -> new AuthException("User not found!!!"));
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            tokenService.saveUserToken(user.getLogin(), new UserToken(accessToken, refreshToken));
            return new LoginResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Incorrect password!!!");
        }
    }

    public LoginResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            var claims = jwtProvider.getRefreshClaims(refreshToken);
            var login = claims.getSubject();
            var userToken = tokenService.getUserToken(login);
            if (userToken != null && userToken.refreshToken().equals(refreshToken)) {
                final User user = userRepository.findByLogin(login)
                        .orElseThrow(() -> new AuthException("User not found"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new LoginResponse(accessToken, null);
            }
        }
        return new LoginResponse(null, null);
    }
}
