package com.example.tp_air.services;

import com.example.tp_air.dto.UserCreateDTO;
import com.example.tp_air.dto.UserDTO;
import com.example.tp_air.exceptions.NotFoundException;
import com.example.tp_air.mappers.UserMapper;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    public UserDTO create(UserCreateDTO createDTO) {
        if (userRepository.findByUsername(createDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = userMapper.toEntity(createDTO);
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    public UserDTO update(Long id, UserCreateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setUsername(updateDTO.getUsername());
        user.setEmail(updateDTO.getEmail());
        user.setRole(updateDTO.getRole());

        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        User updated = userRepository.save(user);
        return userMapper.toDTO(updated);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}
