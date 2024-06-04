package ru.ob11to.springjwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ob11to.springjwt.dto.LoginRequest;
import ru.ob11to.springjwt.dto.LoginResponse;
import ru.ob11to.springjwt.dto.RefreshJwtToken;
import ru.ob11to.springjwt.service.AuthService;
import ru.ob11to.springjwt.service.BlacklistTokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Аутентификация пользователей")
public class AuthController {

    private final AuthService authService;
    private final BlacklistTokenService blacklistTokenService;

    @Operation(
            operationId = "login",
            summary = "Аутентификация пользователя",
            description = "Проверка пользователя",
            tags = { "Authentication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
                    })
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            operationId = "getNewAccessToken",
            summary = "Get New Access Token",
            tags = { "Authentication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
                    })
            }
    )
    @PostMapping("/token")
    public ResponseEntity<LoginResponse> getNewAccessToken(@RequestBody RefreshJwtToken refreshJwtToken) {
        return ResponseEntity.ok(authService.getAccessToken(refreshJwtToken.getRefreshToken()));
    }

    @Operation(
            operationId = "logout",
            summary = "Logout",
            tags = { "Authentication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK")
            },
            security = {
                    @SecurityRequirement(name = "JWT_auth")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String header) {
        blacklistTokenService.saveTokenBlackList(header);
        return ResponseEntity.ok().build();
    }

}
