package com.example.tracker.service;

import com.example.tracker.dto.TaskDto;
import com.example.tracker.mapper.TaskMapper;
import com.example.tracker.model.Task;
import com.example.tracker.model.User;
import com.example.tracker.repository.TaskRepository;
import com.example.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public Flux<TaskDto> findAll() {
        return taskRepository.findAll()
                .flatMap(this::enrichTask)
                .map(taskMapper::toDto)
                .flatMap(this::enrichTaskDto);
    }

    public Mono<TaskDto> findById(String id) {
        return taskRepository.findById(id)
                .flatMap(this::enrichTask)
                .map(taskMapper::toDto)
                .flatMap(this::enrichTaskDto);
    }

    public Mono<TaskDto> create(TaskDto dto) {
        Task task = taskMapper.toEntity(dto);
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        if (dto.getAuthor() != null) {
            task.setAuthorId(dto.getAuthor().getId());
        }
        if (dto.getAssignee() != null) {
            task.setAssigneeId(dto.getAssignee().getId());
        }
        if (dto.getObservers() != null) {
            task.setObserverIds(
                    dto.getObservers().stream()
                            .map(o -> o.getId())
                            .collect(Collectors.toSet())
            );
        }

        return taskRepository.save(task)
                .flatMap(this::enrichTask)
                .map(taskMapper::toDto)
                .flatMap(this::enrichTaskDto);
    }

    public Mono<TaskDto> update(String id, TaskDto dto) {
        return taskRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    existing.setStatus(dto.getStatus());
                    existing.setUpdatedAt(Instant.now());

                    if (dto.getAuthor() != null) {
                        existing.setAuthorId(dto.getAuthor().getId());
                    }
                    if (dto.getAssignee() != null) {
                        existing.setAssigneeId(dto.getAssignee().getId());
                    }
                    if (dto.getObservers() != null) {
                        existing.setObserverIds(
                                dto.getObservers().stream()
                                        .map(o -> o.getId())
                                        .collect(Collectors.toSet())
                        );
                    }

                    return taskRepository.save(existing);
                })
                .flatMap(this::enrichTask)
                .map(taskMapper::toDto)
                .flatMap(this::enrichTaskDto);
    }

    public Mono<TaskDto> addObserver(String taskId, String userId) {
        return taskRepository.findById(taskId)
                .flatMap(task -> {
                    Set<String> ids = task.getObserverIds();
                    ids.add(userId);
                    task.setObserverIds(ids);
                    task.setUpdatedAt(Instant.now());
                    return taskRepository.save(task);
                })
                .flatMap(this::enrichTask)
                .map(taskMapper::toDto)
                .flatMap(this::enrichTaskDto);
    }

    public Mono<Void> delete(String id) {
        return taskRepository.deleteById(id);
    }


    private Mono<Task> enrichTask(Task task) {

        Mono<User> authorMono = task.getAuthorId() != null
                ? userRepository.findById(task.getAuthorId())
                : Mono.empty();

        Mono<User> assigneeMono = task.getAssigneeId() != null
                ? userRepository.findById(task.getAssigneeId())
                : Mono.empty();

        Mono<Set<User>> observersMono;
        if (task.getObserverIds() != null && !task.getObserverIds().isEmpty()) {
            observersMono = Flux.fromIterable(task.getObserverIds())
                    .flatMap(userRepository::findById)
                    .collect(Collectors.toSet());
        } else {
            observersMono = Mono.just(Set.of());
        }

        return Mono.zip(
                        authorMono.defaultIfEmpty(null),
                        assigneeMono.defaultIfEmpty(null),
                        observersMono
                )
                .map(tuple -> {
                    task.setAuthor(tuple.getT1());
                    task.setAssignee(tuple.getT2());
                    task.setObservers(tuple.getT3());
                    return task;
                });
    }

    private Mono<TaskDto> enrichTaskDto(TaskDto dto) {

        Mono<User> authorMono = dto.getAuthor() != null
                ? userRepository.findById(dto.getAuthor().getId())
                : Mono.empty();

        Mono<User> assigneeMono = dto.getAssignee() != null
                ? userRepository.findById(dto.getAssignee().getId())
                : Mono.empty();

        Mono<Set<User>> observersMono;
        if (dto.getObservers() != null && !dto.getObservers().isEmpty()) {
            observersMono = Flux.fromIterable(dto.getObservers())
                    .flatMap(o -> userRepository.findById(o.getId()))
                    .collect(Collectors.toSet());
        } else {
            observersMono = Mono.just(Set.of());
        }

        return Mono.zip(
                        authorMono.defaultIfEmpty(null),
                        assigneeMono.defaultIfEmpty(null),
                        observersMono
                )
                .map(tuple -> {
                    if (tuple.getT1() != null) {
                        dto.setAuthor(new com.example.tracker.dto.UserDto(
                                tuple.getT1().getId(),
                                tuple.getT1().getUsername(),
                                tuple.getT1().getEmail()
                        ));
                    }

                    if (tuple.getT2() != null) {
                        dto.setAssignee(new com.example.tracker.dto.UserDto(
                                tuple.getT2().getId(),
                                tuple.getT2().getUsername(),
                                tuple.getT2().getEmail()
                        ));
                    }

                    dto.setObservers(
                            tuple.getT3().stream()
                                    .map(u -> new com.example.tracker.dto.UserDto(
                                            u.getId(),
                                            u.getUsername(),
                                            u.getEmail()
                                    ))
                                    .collect(Collectors.toSet())
                    );

                    return dto;
                });
    }
}