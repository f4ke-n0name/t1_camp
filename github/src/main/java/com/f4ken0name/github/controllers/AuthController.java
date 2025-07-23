package com.f4ken0name.github.controllers;

import com.f4ken0name.github.models.User;
import com.f4ken0name.github.services.AuthService;
import com.f4ken0name.github.services.JwtHelper;
import com.f4ken0name.github.services.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {
    private final AuthService authService;
    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService,
                          JwtHelper jwtHelper,
                          AuthenticationManager authenticationManager,
                          RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtHelper = jwtHelper;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String login,
                                   @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );
        User user = (User) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String accessToken = jwtHelper.createToken(claims, user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String email,
                                      @RequestParam String login,
                                      @RequestParam String password) {
        User newUser = authService.registerUser(email, login, password);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtHelper.extractToken(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid authorization header");
        }

        String email = jwtHelper.extractEmail(token);
        refreshTokenService.revokeRefreshTokenForUser(email);
        return ResponseEntity.ok("Logged out successfully");
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        String email = refreshTokenService.validateRefreshToken(refreshToken);
        User user = (User) authService.loadUserByUsername(email);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String newAccessToken = jwtHelper.createToken(claims, user.getEmail());
        String newRefreshToken = refreshTokenService.renewRefreshToken(refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refresh_token", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }
}