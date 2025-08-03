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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;

    @Override
    public User getUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public User createUser(User newUser, String rawPassword) {
        checkIfEmailWasNotRegisteredYet(newUser.getEmail());

        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(Role.ROLE_USER);

        User createdUser = userRepository.save(newUser);
        if (createdUser.getRole().equals(Role.ROLE_USER)) {
            eventPublisher.publishEvent(createdUser, Action.CREATED);
        }

        return createdUser;
    }

    @Override
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
    public void deleteUser(Long userId) {
        User userForDelete = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(userForDelete);

        if (userForDelete.getRole().equals(Role.ROLE_USER)) {
            eventPublisher.publishEvent(userForDelete, Action.DELETED);
        }
    }

    @Override
    public User authenticateUser(AuthDto authDto) {
        User user = userRepository.findUserByEmail(authDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException(authDto.getEmail()));

        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    private void checkIfEmailWasNotRegisteredYet(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }
}