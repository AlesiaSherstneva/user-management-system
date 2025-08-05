package com.example.controller;

import com.example.dto.UserResponseDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.AccessDeniedException;
import com.example.model.User;
import com.example.security.JwtProvider;
import com.example.service.UserService;
import com.example.service.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@RequestHeader("Authorization") String jwtToken) {
        log.info("Request for retrieve current user profile");
        User currentUser = getUserFromJwtToken(jwtToken);
        log.info("Returning user profile: {}", currentUser.getEmail());

        return ResponseEntity.ok(userMapper.userToResponseDto(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long userId,
                                                       @RequestHeader("Authorization") String jwtToken) {
        log.info("Request for retrieve user profile with id: {}", userId);
        User currentUser = getUserFromJwtToken(jwtToken);

        checkIfAdminOrCurrentUser(currentUser, userId);

        User receivedUser = userService.getUserById(userId);
        log.info("Returning user profile: {}", receivedUser.getEmail());

        return ResponseEntity.ok(userMapper.userToResponseDto(receivedUser));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> editCurrentUser(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                                           @RequestHeader("Authorization") String jwtToken) {
        log.info("Request for update current user profile");
        User currentUser = getUserFromJwtToken(jwtToken);

        User updatedUser = userService.updateUser(currentUser.getId(), userUpdateDto);
        log.info("User profile {} updated successfully by owner", updatedUser.getEmail());

        return ResponseEntity.ok(userMapper.userToResponseDto(updatedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> editUserById(@PathVariable("id") Long userId,
                                                        @Valid @RequestBody UserUpdateDto userUpdateDto,
                                                        @RequestHeader("Authorization") String jwtToken) {
        log.info("Request for update user profile with id: {}", userId);
        User currentUser = getUserFromJwtToken(jwtToken);
        log.info("Modifying user account id: {}, initiated by: {} with role: {}",
                userId, currentUser.getEmail(), currentUser.getRole());

        checkIfAdminOrCurrentUser(currentUser, userId);

        if (isUserAdmin(currentUser) && !currentUser.getId().equals(userId)) {
            userUpdateDto.setEmail(null);
            userUpdateDto.setPassword(null);
            log.debug("Admin restricted from changing credentials for other users");
        }

        User updatedUser = userService.updateUser(userId, userUpdateDto);
        log.info("Profile updated successfully for user: {}, by: {} with role: {}",
                updatedUser.getEmail(), currentUser.getEmail(), currentUser.getRole());

        return ResponseEntity.ok(userMapper.userToResponseDto(updatedUser));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@RequestHeader("Authorization") String jwtToken) {
        log.warn("Request for delete current user profile");
        User currentUser = getUserFromJwtToken(jwtToken);

        userService.deleteUser(currentUser.getId());
        log.warn("User profile {} deleted by owner", currentUser.getEmail());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long userId,
                                               @RequestHeader("Authorization") String jwtToken) {
        log.info("Request for delete user profile with id: {}", userId);
        User currentUser = getUserFromJwtToken(jwtToken);
        log.warn("Deleting user account id: {}, initiated by: {} with role: {}",
                userId, currentUser.getEmail(), currentUser.getRole());

        checkIfAdminOrCurrentUser(currentUser, userId);

        userService.deleteUser(userId);
        log.info("User profile with id {} deleted by: {} with role: {}",
                userId, currentUser.getEmail(), currentUser.getRole());

        return ResponseEntity.noContent().build();
    }

    private User getUserFromJwtToken(String jwtToken) {
        return userService.getUserById(jwtProvider.getUserIdFromToken(jwtToken.substring(7)));
    }

    private void checkIfAdminOrCurrentUser(User user, Long userId) {
        if (!user.getId().equals(userId) && !isUserAdmin(user)) {
            log.warn("Access denied: user {} action on id {}", user.getEmail(), userId);
            throw new AccessDeniedException();
        }
    }

    private boolean isUserAdmin(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}