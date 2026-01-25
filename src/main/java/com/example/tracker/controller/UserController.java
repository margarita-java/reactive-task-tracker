package com.example.tracker.controller;

import com.example.tracker.dto.UserDto;
import com.example.tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Flux<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<UserDto> findById(@PathVariable String id) {
        return userService.findById(id);
    }

    @PostMapping
    public Mono<UserDto> create(@RequestBody UserDto dto) {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    public Mono<UserDto> update(@PathVariable String id,
                                @RequestBody UserDto dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return userService.delete(id);
    }
}