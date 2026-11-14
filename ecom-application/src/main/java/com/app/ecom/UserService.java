package com.app.ecom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    void addUser(User user) {
        userRepository.save(user);
    }

    Optional<User> fetchUser(Long id) {
        return userRepository.findById(id);
    }

    boolean updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            userRepository.save(existingUser);
            return true;
        }).orElse(false);
    }
}
