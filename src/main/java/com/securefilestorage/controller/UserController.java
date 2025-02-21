package com.securefilestorage.controller;

import com.securefilestorage.dto.UserRequestDto;
import com.securefilestorage.dto.UserResponseDto;
import com.securefilestorage.model.User;
import com.securefilestorage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for user operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param userRequestDto DTO containing user registration details.
     * @return Registered user details as a DTO.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto userRequestDto) {
        // Convert the DTO to an entity
        User user = convertToEntity(userRequestDto);
        // Save the user using the service
        User registeredUser = userService.registerUser(user);
        // Convert the saved entity to a response DTO and return it
        return ResponseEntity.ok(convertToDto(registeredUser));
    }

    /**
     * Retrieves user by login.
     *
     * @param login Login to search.
     * @return User details as a DTO if found.
     */
    @GetMapping("/{login}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String login) {
        Optional<User> user = userService.findByLogin(login);
        return user.map(u -> ResponseEntity.ok(convertToDto(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId ID of the user to delete.
     * @return Response entity.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/info")
    public ResponseEntity<String> getUserInfo() {
        return ResponseEntity.ok("User controller info");
    }

    /**
     * Converts a UserRequestDto to a User entity.
     *
     * @param dto the DTO with registration data.
     * @return a User entity.
     */
    private User convertToEntity(UserRequestDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        // Optionally, if not using @CreationTimestamp or @PrePersist,
        // you can set createdAt here, though it’s better to let the entity handle it.
        return user;
    }

    /**
     * Converts a User entity to a UserResponseDto.
     *
     * @param user the User entity.
     * @return the corresponding response DTO.
     */
    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        return dto;
    }

}