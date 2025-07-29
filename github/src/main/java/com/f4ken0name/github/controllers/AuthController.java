package com.f4ken0name.github.controllers;

import com.f4ken0name.github.models.User;
import com.f4ken0name.github.services.AuthService;
import com.f4ken0name.github.services.JweHelper;
import com.f4ken0name.github.services.RefreshTokenService;
import com.f4ken0name.github.utils.BlackListImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JweHelper jweHelper;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private BlackListImpl blackList;

    public AuthController(AuthService authService,
                          JweHelper jweHelper,
                          AuthenticationManager authenticationManager,
                          RefreshTokenService refreshTokenService, BlackListImpl blackList) {
        this.authService = authService;
        this.jweHelper = jweHelper;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.blackList = blackList;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String login,
                                   @RequestParam String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password));
        User user = (User) auth.getPrincipal();

        String accessToken = jweHelper.generateEncryptedToken(user.getEmail(), user.getRole().toString());
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
        authService.registerUser(email, login, password);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or malformed Authorization header");
        }

        String token = header.substring(7);

        try {
            String email = jweHelper.getUsernameFromToken(token);
            refreshTokenService.revokeRefreshTokenForUser(email);
            blackList.add(token);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        String email = refreshTokenService.validateRefreshToken(refreshToken);
        User user = (User) authService.loadUserByUsername(email);

        String newAccessToken = jweHelper.generateEncryptedToken(user.getEmail(), user.getRole().toString());
        String newRefreshToken = refreshTokenService.renewRefreshToken(refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refresh_token", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }
}
