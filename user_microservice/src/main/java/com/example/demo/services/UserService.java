package com.example.demo.services;


import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserDetailsDTO;
import com.example.demo.dtos.builders.UserBuilder;
import com.example.demo.entities.User;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findPersons() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO findPersonById(UUID id) {
        Optional<User> prosumerOptional = userRepository.findById(id);
        if (prosumerOptional.isEmpty()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toPersonDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(UserDetailsDTO personDTO) {
        User user = UserBuilder.toEntity(personDTO);
        user = userRepository.save(user);
        LOGGER.debug("Person with id {} was inserted in db", user.getId());
        return user.getId();
    }
    public void delete(UUID id) {
        UserDetailsDTO user = findPersonById(id);

        if ("admin".equalsIgnoreCase(user.getName())) {
            LOGGER.warn("Attempted to delete admin user with id {}", id);
            throw new IllegalArgumentException("Cannot delete admin user");
        }

        userRepository.deleteById(id);
        LOGGER.info("User with id {} deleted from database", id);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String authServiceUrl = "http://auth-service:8083/auth/credentials/" + user.getCredentialId();
            restTemplate.delete(authServiceUrl);
            LOGGER.info("Deleted associated credentials with id {}", user.getCredentialId());
        } catch (Exception e) {
            LOGGER.error("Failed to delete credentials for user {}: {}", id, e.getMessage());
        }
    }

}
