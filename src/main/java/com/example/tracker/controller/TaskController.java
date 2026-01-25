package com.example.tracker.controller;

import com.example.tracker.dto.TaskDto;
import com.example.tracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Flux<TaskDto> findAll() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<TaskDto> findById(@PathVariable String id) {
        return taskService.findById(id);
    }

    @PostMapping
    public Mono<TaskDto> create(@RequestBody TaskDto dto) {
        return taskService.create(dto);
    }

    @PutMapping("/{id}")
    public Mono<TaskDto> update(@PathVariable String id,
                                @RequestBody TaskDto dto) {
        return taskService.update(id, dto);
    }

    @PostMapping("/{taskId}/observers/{userId}")
    public Mono<TaskDto> addObserver(@PathVariable String taskId,
                                     @PathVariable String userId) {
        return taskService.addObserver(taskId, userId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return taskService.delete(id);
    }
}