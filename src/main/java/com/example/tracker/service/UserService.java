package com.example.tracker.service;

import com.example.tracker.dto.UserDto;
import com.example.tracker.mapper.UserMapper;
import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Flux<UserDto> findAll() {
        return userRepository.findAll()
                .map(this::toDtoManual);
    }

    public Mono<UserDto> findById(String id) {
        return userRepository.findById(id)
                .map(this::toDtoManual);
    }

    public Mono<UserDto> create(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        return userRepository.save(user)
                .map(this::toDtoManual);
    }

    public Mono<UserDto> update(String id, UserDto dto) {
        return userRepository.findById(id)
                .flatMap(existing -> {
                    existing.setUsername(dto.getUsername());
                    existing.setEmail(dto.getEmail());
                    return userRepository.save(existing);
                })
                .map(this::toDtoManual);
    }

    public Mono<Void> delete(String id) {
        return userRepository.deleteById(id);
    }

    private UserDto toDtoManual(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}