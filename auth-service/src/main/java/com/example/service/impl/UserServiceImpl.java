package com.example.service.impl;

import com.example.dto.AuthDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.EmailAlreadyRegisteredException;
import com.example.exception.InvalidCredentialsException;
import com.example.exception.UserNotFoundException;
import com.example.kafka.UserEventPublisher;
import com.example.kafka.event.enums.Action;
import com.example.model.User;
import com.example.model.enums.Role;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        User receivedUser = userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Lookup failed. User with id {} not found", userId);
                    throw new UserNotFoundException(userId);
                });
        log.info("Returning user profile: {}", receivedUser.getEmail());

        return receivedUser;
    }

    @Override
    @Transactional
    public User createUser(User newUser, String rawPassword) {
        checkIfEmailWasNotRegisteredYet(newUser.getEmail());

        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(Role.ROLE_USER);
        log.debug("Password encoded and role set for new user: {}", newUser.getEmail());

        User createdUser = userRepository.save(newUser);
        log.info("New user {} registered with id: {}", createdUser.getEmail(), createdUser.getId());

        if (createdUser.getRole().equals(Role.ROLE_USER)) {
            eventPublisher.publishEvent(createdUser, Action.CREATED);
        }

        return createdUser;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User userToUpdate = getUserById(userId);

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(userToUpdate.getEmail())) {
            checkIfEmailWasNotRegisteredYet(userUpdateDto.getEmail());
            userToUpdate.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getFirstName() != null) {
            userToUpdate.setFirstName(userUpdateDto.getFirstName());
        }
        if (userUpdateDto.getLastName() != null) {
            userToUpdate.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        User updatedUser = userRepository.save(userToUpdate);

        if (updatedUser.getRole().equals(Role.ROLE_USER)) {
            eventPublisher.publishEvent(updatedUser, Action.UPDATED);
        }

        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User userForDelete = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(userForDelete);

        if (userForDelete.getRole().equals(Role.ROLE_USER)) {
            eventPublisher.publishEvent(userForDelete, Action.DELETED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticateUser(AuthDto authDto) {
        User user = userRepository.findUserByEmail(authDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("Authentication failed. User with email {} not found", authDto.getEmail());
                    throw new UserNotFoundException(authDto.getEmail());
                });

        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            log.warn("Authentication failed. Invalid password for email {}", authDto.getEmail());
            throw new InvalidCredentialsException();
        }

        log.debug("Authenticated user: {}, role: {}", user.getEmail(), user.getRole());

        return user;
    }

    private void checkIfEmailWasNotRegisteredYet(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Operation rejected: email {} already registered", email);
            throw new EmailAlreadyRegisteredException(email);
        }
    }
}