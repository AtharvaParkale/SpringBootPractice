package com.project.fitness.service;

import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.model.User;
import com.project.fitness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .password(request.getPassword())
                .build();
//        User user = new User(null, request.getEmail(), request.getPassword(), request.getFirstName(), request.getLastName(), Instant.parse("2026-07-23T12:44:39.214Z").atZone(ZoneOffset.UTC).toLocalDateTime(), Instant.parse("2026-07-23T12:44:39.214Z").atZone(ZoneOffset.UTC).toLocalDateTime(), List.of(), List.of());

        User savedUser = userRepository.save(user);

        return mappedTo(savedUser);
    }

    private UserResponse mappedTo(User savedUser) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getId());
        userResponse.setPassword(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userResponse;

    }
}
