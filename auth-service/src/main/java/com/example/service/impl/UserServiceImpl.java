package com.example.service.impl;

import com.example.dto.AuthDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.EmailAlreadyRegisteredException;
import com.example.exception.InvalidCredentialsException;
import com.example.exception.UserNotFoundException;
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

        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User updatingUser = getUserById(userId);

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(updatingUser.getEmail())) {
            checkIfEmailWasNotRegisteredYet(userUpdateDto.getEmail());
            updatingUser.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getFirstName() != null) {
            updatingUser.setFirstName(userUpdateDto.getFirstName());
        }
        if (userUpdateDto.getLastName() != null) {
            updatingUser.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getPassword() != null) {
            updatingUser.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        return userRepository.save(updatingUser);
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

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void checkIfEmailWasNotRegisteredYet(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }
}