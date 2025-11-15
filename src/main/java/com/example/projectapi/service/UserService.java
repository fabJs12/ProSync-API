package com.example.projectapi.service;

import com.example.projectapi.model.User;
import com.example.projectapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        return userRepository.save(user);
    }

    public User update(Integer id, User updatedUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(updatedUser.getUsername());
                    if (!existing.getEmail().equals(updatedUser.getEmail())
                            && userRepository.existsByEmail(updatedUser.getEmail())) {
                        throw new RuntimeException("El email ya está registrado");
                    }
                    existing.setEmail(updatedUser.getEmail());
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        existing.setPassword(updatedUser.getPassword());
                    }
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}