package dev.aziz.course.controllers;

import dev.aziz.course.config.UserAuthProvider;
import dev.aziz.course.dtos.CredentialsDto;
import dev.aziz.course.dtos.SignUpDto;
import dev.aziz.course.dtos.UserDto;
import dev.aziz.course.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody CredentialsDto credentialsDto) {
        UserDto user = userService.login(credentialsDto);
        user.setToken(userAuthProvider.createToken(user.getEmail()));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpDto signUpDto) {
        UserDto user = userService.register(signUpDto);
        user.setToken(userAuthProvider.createToken(user.getEmail()));
        return ResponseEntity.created(URI.create("/users" + user.getId()))
                .body("User successfully created. Plesase check your email and verify.");
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> logOut(@AuthenticationPrincipal UserDto userDto) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("activationCode") String activationCode) {
        Boolean isSuccess = userService.verifyActivationCode(activationCode);
        return ResponseEntity.ok("Account Verified. Success: " + isSuccess);
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("New password create link was sent to your email. ");
    }

    @GetMapping("/new-password")
    public ResponseEntity<?> newPassword(@RequestParam("secretCode") String secretCode,
                                         @RequestParam("newPassword") String newPassword) {
        userService.createNewPassword(secretCode, newPassword);
        return ResponseEntity.ok("New Password created. ");
    }

}
