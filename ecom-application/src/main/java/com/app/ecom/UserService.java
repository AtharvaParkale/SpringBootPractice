package com.app.ecom;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final List<User> userList = new ArrayList<>();
    private Long nextId = 1L;

    List<User> fetchAllUsers() {
        return userList;
    }

    void addUser(User user) {
        user.setId(nextId++);
        userList.add(user);
    }

    Optional<User> fetchUser(Long id) {
        return userList.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
    }

    boolean updateUser(Long id, User updatedUser) {
        return fetchUser(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    return true;
                })
                .orElse(false);
    }
}
