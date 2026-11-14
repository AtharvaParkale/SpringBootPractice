package com.app.ecom.service;

import com.app.ecom.dto.AddressDTO;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.model.Address;
import com.app.ecom.model.User;
import com.app.ecom.model.UserRole;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    public void addUser(UserRequest userRequest) {

        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id).map(this::mapToUserResponse);
    }

    public boolean updateUser(Long id, UserRequest updatedUserRequest) {
        return userRepository.findById(id).map(existingUser -> {
            updateUserFromRequest(existingUser, updatedUserRequest);
            userRepository.save(existingUser);
            return true;
        }).orElse(false);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if (userRequest.getAddress() != null) {
            Address address = new Address();
            address.setCity(userRequest.getAddress().getCity());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setState(userRequest.getAddress().getState());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(String.valueOf(user.getId()));
        response.setFirstName(String.valueOf(user.getFirstName()));
        response.setLastName(String.valueOf(user.getLastName()));
        response.setEmail(String.valueOf(user.getEmail()));
        response.setPhone(String.valueOf(user.getPhone()));
        response.setRole(UserRole.valueOf(String.valueOf(user.getRole())));

        if (user.getAddress() != null) {
            AddressDTO address = new AddressDTO();

            address.setCity(user.getAddress().getCity());
            address.setCountry(user.getAddress().getCountry());
            address.setState(user.getAddress().getState());
            address.setZipcode(user.getAddress().getZipcode());
            response.setAddress(address);
        }
        return response;
    }
}
