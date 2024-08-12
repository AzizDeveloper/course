package dev.aziz.course.controllers;

import dev.aziz.course.dtos.UserDto;
import dev.aziz.course.dtos.UserSummaryDto;
import dev.aziz.course.entities.Course;
import dev.aziz.course.entities.User;
import dev.aziz.course.repositories.UserRepository;
import dev.aziz.course.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User successfully deleted.");
    }

    @PostMapping("/{id}/cart")
    public ResponseEntity<?> addCourseToCartById(@AuthenticationPrincipal UserDto userDto, @PathVariable Long id) {
        return ResponseEntity.ok(userService.addCourseToCart(userDto.getId(), id));
    }

    @DeleteMapping("/{id}/cart")
    public ResponseEntity<?> deleteCourseFromCartById(@AuthenticationPrincipal UserDto userDto, @PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteCourseFromCart(userDto.getId(), id));
    }

}
